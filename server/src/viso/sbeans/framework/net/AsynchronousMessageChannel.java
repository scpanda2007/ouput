package viso.sbeans.framework.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static viso.com.util.Objects.checkNull;

public class AsynchronousMessageChannel implements Channel{
	
	AsynchronousByteChannel channel;
	
	private static final int kBufferSize = 1024*10;
	
	private static final int kPrefixLength = 2;
	
	public AsynchronousMessageChannel(AsynchronousByteChannel channel){
		checkNull("channel",channel);
		this.channel = channel;
	}
	
	ByteBuffer readBuffer = ByteBuffer.allocate(kBufferSize);
	
	private AtomicBoolean isReading = new AtomicBoolean(false); 
	
	public Future<MessageBuffer> read(CompletionHandler<MessageBuffer,Void> handler){
		if(!isReading.compareAndSet(false, true)){
			throw new IllegalStateException(" channel is reading. ");
		}
		return new Reader(handler).start();
	}
	
	private int messageLength(){
		if(readBuffer.remaining() < kPrefixLength)return -1;
		return (readBuffer.getShort(0) & 0xffff) + kPrefixLength;
	}
	
	private class Reader extends FutureTask<MessageBuffer> implements CompletionHandler<Integer,Void>{

		private CompletionHandler<MessageBuffer,Void> handler;
		
		private int messageLength = -1;
		
		public Reader(CompletionHandler<MessageBuffer,Void> handler) {
			super(new FailtureCallable<MessageBuffer>());
			// TODO Auto-generated constructor stub
			this.handler = handler;
		}

		public Future<MessageBuffer> start(){
			if(readBuffer.position()>0){
				int len = messageLength();
				if(readBuffer.position()>len){
					readBuffer.limit(readBuffer.position());
					readBuffer.position(len);
					readBuffer.compact();
				}else{
					readBuffer.clear();
				}
			}
			channel.read(readBuffer, null, this);
			return this;
		}
		
		@Override
		public void completed(Integer arg0, Void arg1) {
			if(arg0<0){
				setException(new EOFException(" message incomplete.. "));
				return;
			}
			
			// TODO Auto-generated method stub
			if(messageLength==-1){
				messageLength = messageLength();
				if(messageLength>0 && readBuffer.limit()<messageLength){
					setException(new IllegalStateException(" have not enough room for message."));
				}
			}
			if(messageLength>0 && readBuffer.position()>=messageLength ){
				ByteBuffer result = readBuffer.duplicate();
				result.limit(messageLength);
				result.position(kPrefixLength);
				set(new MessageBuffer(result.slice().array()));
				return;
			}
			channel.read(readBuffer, null, this);
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			readBuffer.clear();
			isReading.set(false);
			setException(arg0);
		}
		
		@Override
		public void done(){
			isReading.set(false);
			if(handler!=null){
				try {
					handler.completed(this.get(), null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(e.getCause() instanceof AsynchronousCloseException){
						try {
							close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
		
		@Override
		public boolean cancel(boolean arg0){
			//当断开一个连接时
			return super.cancel(arg0);
		}
		
	}
	
	public Future<Void> write(ByteBuffer buffer, CompletionHandler<Void,Void> handler){
		if(!isWriting.compareAndSet(false, true)){
			throw new IllegalStateException(" the channel is in writing.");
		}
		return new Writer(buffer,handler).start();
	}
	
	private AtomicBoolean isWriting = new AtomicBoolean(false);
	
	private class Writer extends FutureTask<Void> implements CompletionHandler<Integer,Void>{
		
		private ByteBuffer waitSize;
		
		CompletionHandler<Void,Void> handler;
		
		public Writer(ByteBuffer sendbuffer,CompletionHandler<Void,Void> handler) {
			super(new FailtureCallable<Void>());
			// TODO Auto-generated constructor stub
			short len = (short)sendbuffer.remaining();
			waitSize = ByteBuffer.allocate(2+len);
			waitSize.putShort(len);
			waitSize.put(sendbuffer);
			waitSize.flip();
			this.handler = handler;
		}
		
		public Future<Void> start(){
			channel.write(waitSize, null, this);
			return this;
		}
		
		@Override
		public void completed(Integer arg0, Void arg1) {
			// TODO Auto-generated method stub
			if(!waitSize.hasRemaining()){
				set(null);
				return;
			}
			channel.write(waitSize, null, this);
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			setException(arg0);
		}
		
		@Override
		public void done(){
			if(handler!=null){
				try {
					handler.completed(this.get(), null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	public void close() throws IOException{
		channel.close();
	}
	
	private class FailtureCallable<V> implements Callable<V>{

		@Override
		public V call() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return channel.isOpen();
	}
}
