package viso.sbeans.framework.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static viso.com.util.Objects.checkNull;
//这里还有个需要注意的地方，就是decode出来的消息的派发给业务处理器工作最好交给一个线程池来处理
//，避免阻塞group绑定的线程池。
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
		if(readBuffer.position() < kPrefixLength)return -1;
		return (readBuffer.getShort(0) & 0xffff) + kPrefixLength;
	}
	
	private class Reader extends FutureTask<MessageBuffer> implements CompletionHandler<Integer,Void>{

		private CompletionHandler<MessageBuffer,Void> handler;
		
		private int messageLength = -1;
		
		private final Object lock = new Object();
		
		public Reader(CompletionHandler<MessageBuffer,Void> handler) {
			super(new FailtureCallable<MessageBuffer>());
			// TODO Auto-generated constructor stub
			this.handler = handler;
		}

		public Future<MessageBuffer> start() {
			synchronized (lock) {
				if(isDone())return this;
				if (readBuffer.position() > 0) {
					int len = messageLength();
					if (readBuffer.position() > len) {
						readBuffer.limit(readBuffer.position());
						readBuffer.position(len);
						readBuffer.compact();
					} else {
						readBuffer.clear();
					}
				}
				processBuffer();
				return this;
			}
		}
		
		public void processBuffer(){
			// TODO Auto-generated method stub
			if(messageLength==-1){
				messageLength = messageLength();
				if(messageLength>0 && readBuffer.limit()<messageLength){
					setException(new BufferOverflowException());
					return;
				}
			}
			if(messageLength>0 && readBuffer.position()>=messageLength ){
				ByteBuffer result = readBuffer.duplicate();
				result.limit(messageLength);
				result.position(kPrefixLength);
				ByteBuffer message = result.slice().asReadOnlyBuffer();
				byte payload[] = new byte[message.remaining()];
				message.get(payload);
				set(new MessageBuffer(payload));
				return;
			}
			channel.read(readBuffer, null, this);
			//己方channel关闭后如何处理，这里会直接抛出一个ClosedException
			//对方channel关闭后，这里会收到一个-1的通知，但是如果底层对方的数据还未发完就被强行关闭
			//，还想去读时，这里会抛出一个IOException
		}
		
		@Override
		public void completed(Integer arg0, Void arg1) {
			synchronized (lock) {
				if(isDone())return;
				if(arg0.intValue() < 0){
					setException(new EOFException("incomplete message"));
					//但是此时有可能有一部分的数据还没从buffer中读完,对面的端口就关掉了 这个地方会以高优先级立即返回 那么就只读到了一部分的信息
					//同时如果大量用户同时close的话group就会生成很多并发thread来响应关闭
					//TODO: 测试里添加异常测试
					return;
				}
				processBuffer();
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			synchronized (lock) {
				// TODO Auto-generated method stub
//				readBuffer.clear();
//				isReading.set(false);
				setException(arg0);
			}
		}
		
		@Override
		public void done() {
			synchronized (lock) {
				isReading.set(false);
				if (handler != null) {
					try {
						handler.completed(this.get(), null);
					} catch (InterruptedException e) {
						setException(e);
					} catch (ExecutionException e) {
						setException(e.getCause()==null?e:e.getCause());
					} catch (Throwable t){
						setException(t);
					}
				}
			}
		}
		
		@Override
		public boolean cancel(boolean arg0){
			synchronized (lock) {
				// 当断开一个连接时
				return super.cancel(arg0);
			}
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
		
		private final Object lock = new Object();
		
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
		
		public Future<Void> start() {
			synchronized (lock) {
				channel.write(waitSize, null, this);
				return this;
			}
		}
		
		@Override
		public void completed(Integer arg0, Void arg1) {
			synchronized (lock) {
				// TODO Auto-generated method stub
				if (!waitSize.hasRemaining()) {
					set(null);
					return;
				}
				channel.write(waitSize, null, this);
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			synchronized (lock) {
				// TODO Auto-generated method stub
//				arg0.printStackTrace();
				//TODO: 测试里添加异常测试 己方未发完数据 就强制关闭，
				//这里会生成一个AyschronousCloseException
				//对方关闭 未测试
				setException(arg0);
			}
		}
		
		@Override
		public void done() {
			synchronized (lock) {
				isWriting.set(false);
				if (handler != null) {
					try {
						handler.completed(this.get(), null);
					} catch (InterruptedException e) {
						setException(e);
					} catch (ExecutionException e) {
						setException(e.getCause()==null?e:e.getCause());
					} catch (Throwable t){
						setException(t);
					}
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
