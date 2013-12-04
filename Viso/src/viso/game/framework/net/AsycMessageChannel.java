package viso.game.framework.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static viso.com.util.Objects.checkNull;
/**
 * 异步方式读取或发送消息的管道
 * */
public class AsycMessageChannel {
	
	private AsynchronousByteChannel channel;
	
	/** 每个消息附带的前缀长度. */
	public static final int PREFIX_LENGTH = 2;
	
	public AsycMessageChannel(AsynchronousByteChannel channel, int buffersize){
		checkNull("Asy ByteChannel ", channel);
		this.channel = channel;
		this.readBuffer = ByteBuffer.allocateDirect(buffersize);
	}
	
	public Future<Integer> write(ByteBuffer buffer, CompletionHandler<Integer,Void> handler){
		if(!isWriting.compareAndSet(false, true)){
			throw new IllegalStateException("The Asyc Channel is in writing.");
		}
		Writer writer = new Writer(handler,buffer);
		writer.startWrite();
		return writer;
	}
	
	public Future<ByteBuffer> read(CompletionHandler<ByteBuffer,Integer> handler){
		if(!isReading.compareAndSet(false, true)){
			throw new IllegalStateException("The Asyc Channel is in reading.");
		}
		Reader reader = new Reader(handler);
		reader.startRead();
		return reader;
	}
	
	/** 失败则抛出异常. */
	private static final class FailingCallable<V> implements Callable<V> {
		FailingCallable() {
		}

		public V call() {
			throw new AssertionError();
		}
	}
	
	final ByteBuffer readBuffer;
	
	AtomicBoolean isReading = new AtomicBoolean(false);
	
	private class Reader extends FutureTask<ByteBuffer> implements CompletionHandler<Integer, Void>{
		
		CompletionHandler<ByteBuffer, Integer> handler;
		
		private int lastMessageLength = -1;
		
		private int lastMessageLength(){
			return readBuffer.position() > PREFIX_LENGTH ? ((readBuffer.getShort(0) & 0xffff) + PREFIX_LENGTH) : -1;
		}
		
		public Reader(CompletionHandler<ByteBuffer, Integer> handler) {
			super(new FailingCallable<ByteBuffer>());
			// TODO Auto-generated constructor stub
			this.handler = handler;
		}
		
		public void startRead(){
			readBuffer.clear();
			int position = readBuffer.position();
			if(position > 0){
				int lstMessageLength = lastMessageLength();
				assert lstMessageLength > 0;
				if(position > lstMessageLength){
					//在上一次读取消息时顺带读了一部分以后的消息
					readBuffer.position(lstMessageLength);//移除掉已经读了的消息
					readBuffer.limit(position);
					readBuffer.compact();
				}else{
					readBuffer.clear();
				}
			}
			channel.read(readBuffer, null, this);
		}

		@Override
		protected void done(){
			isReading.set(false);
			if(this.handler!=null){
				try {
					this.handler.completed(this.get(), null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					setException(e);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					setException(e);
				}
			}
		}
		
		@Override
		public void completed(Integer result, Void attachment) {
			
			if(result < 0){
				setException(new EOFException(" The message is incomplete."));
				return;
			}
			
			// TODO Auto-generated method stub
			if(lastMessageLength<0){
				//第一次读
				lastMessageLength = lastMessageLength();
				if(lastMessageLength>=0 && readBuffer.limit() < lastMessageLength){
					setException(new BufferOverflowException());//消息太多存不下
					return;
				}
			}
			if(lastMessageLength>=0 && readBuffer.position() >= lastMessageLength){
				ByteBuffer buffer = readBuffer.duplicate();
				buffer.limit(lastMessageLength);
				buffer.position(PREFIX_LENGTH);
				set(buffer.slice().asReadOnlyBuffer());
			}else{
				channel.read(readBuffer, null, this);
			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub
		}
	}
	
	final AtomicBoolean isWriting = new AtomicBoolean(false);
	
	private class Writer extends FutureTask<Integer> implements CompletionHandler<Integer, Void>{

		CompletionHandler<Integer, Void> handler;
		
		private final ByteBuffer sendBuffer;
		
		public Writer(CompletionHandler<Integer, Void> handler, ByteBuffer src) {
			super(new FailingCallable<Integer>());
			// TODO Auto-generated constructor stub
			this.handler = handler;
			int size = src.remaining();
			assert size < Short.MAX_VALUE;
			sendBuffer = ByteBuffer.allocate(PREFIX_LENGTH + size);
			sendBuffer.putShort((short)size).put(src).flip();
		}
		
		public void startWrite(){
			channel.write(sendBuffer, null, this);
		}

		@Override
		protected void done(){
			isWriting.set(false);
			if(handler!=null){
				try {
					handler.completed(this.get(), null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					setException(e);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					setException(e);
				}
			}
			return;
		}
		
		@Override
		public void completed(Integer result, Void attachment) {
			// TODO Auto-generated method stub
			if(sendBuffer.hasRemaining()){
				channel.write(sendBuffer, null, this);
				return;
			}
			set(null);
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public boolean isOpen(){
		return channel.isOpen();
	}
	
	public void close() throws IOException{
		channel.close();
	}
}
