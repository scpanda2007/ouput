package viso.game.framework.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import viso.com.util.NamedThreadFactory;

public class Transport {

	AsynchronousChannelGroup channelGroup;

	AsynchronousServerSocketChannel acceptor;

	private static final int kDefaultBackLog = 0;

	private static final int kDefaultPort = 12345;

	private InetSocketAddress listenAddress;
	
	private int backlog;
	
	public Transport(Properties property) {
		String hostname = "127.0.0.1";
		int port = kDefaultPort;
		try {
			AsynchronousChannelProvider provider = AsynchronousChannelProvider
					.provider();
			channelGroup = provider.openAsynchronousChannelGroup(Executors
					.newCachedThreadPool(new NamedThreadFactory(
							"Transport-Accetpor")), 1);
			listenAddress = new InetSocketAddress(hostname, port);
			backlog = kDefaultBackLog;
			acceptor = AsynchronousChannelProvider.provider().openAsynchronousServerSocketChannel(channelGroup);
			acceptor.bind(listenAddress, backlog);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void accept(ConnectionHandler listener){
		acceptor.accept(null, new AcceptorHandler(listener));
	}
	
	private class AcceptorHandler implements CompletionHandler<AsynchronousSocketChannel,Void>{
		
		ConnectionHandler listener;
		
		AcceptorHandler(ConnectionHandler listener){
			this.listener = listener;
		}

		@Override
		public void completed(AsynchronousSocketChannel arg0, Void arg1) {
			// TODO Auto-generated method stub
			AsycMessageChannel channel = new AsycMessageChannel(arg0, 1024*10);
			listener.newChannel(channel);//TODO:如果检测到内部线程抛出异常 这里不应该被终端
			acceptor.accept(null, AcceptorHandler.this);
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			shutdown();//TODO:如果检测到内部线程抛出异常 这里不应该被终端
		}
		
	}
	
	public void restart() throws IOException{
		if(channelGroup.isShutdown()){
			throw new IOException("The channel group has shutdown");
		}
		try{
			acceptor.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		acceptor = AsynchronousChannelProvider.provider().openAsynchronousServerSocketChannel(channelGroup);
		acceptor.bind(listenAddress, backlog);
	}
	
	public void shutdown(){
		if(acceptor!=null && acceptor.isOpen()){
			try {
				acceptor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(channelGroup!=null && !channelGroup.isShutdown()){
			channelGroup.shutdown();
			boolean groupShutdownCompleted = false;
			try {
				groupShutdownCompleted = channelGroup.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
			}
			
			if(!groupShutdownCompleted){
				try {
					channelGroup.shutdownNow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
