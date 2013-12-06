package viso.sbeans.framework.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static viso.com.util.Objects.checkNull;
import viso.com.util.NamedThreadFactory;
import viso.com.util.PropertiesWrapper;

public class TcpTransport {
	
	public interface ConnectionListener {
		/**tcp层收到一条新的连接时*/
		public void newConnection(AsynchronousMessageChannel channel) throws Exception;
		/**tcp层关闭时*/
		public void shutdown();
	}
	
	AsynchronousServerSocketChannel acceptor;//服务器侦听
	AsynchronousChannelGroup group;
	InetSocketAddress listenAddress;//侦听地址
	
	public static final String PKG_NAME = "viso.sbeans.framework.net.tcp";
	public static final String ADD_HOST = PKG_NAME+"host";
	public static final String ADD_PORT = PKG_NAME+"port"; 
	
	public static final String kDefaultHost = "127.0.0.1";
	public static final int kDefaultPort = 22345;
	
	public TcpTransport(Properties property){
		try {
			group = AsynchronousChannelProvider
					.provider()
					.openAsynchronousChannelGroup(
							Executors.newCachedThreadPool(new NamedThreadFactory(
											"tcp-transport")), 1);
			PropertiesWrapper properties = new PropertiesWrapper(property);
			String hostname = properties.getProperty(ADD_HOST, kDefaultHost);
			int port = properties.getIntProperty(ADD_PORT, kDefaultPort, 0, 65535);
			System.out.println("host :: "+hostname+" port :: "+port);
			listenAddress = new InetSocketAddress(hostname, port);
			try {
				acceptor = AsynchronousChannelProvider.provider()
				.openAsynchronousServerSocketChannel(group);
				acceptor.bind(listenAddress, 0);
			} catch (Exception ex) {
				try {
					acceptor.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	ConnectionHandler connHandler;
	
	//开始接听
	public void accept(ConnectionListener listener){
		checkNull("listener",listener);
		if(!acceptor.isOpen()){
			throw new IllegalStateException(" the acceptor is not open");
		}
		if(this.connHandler!=null){
			throw new AcceptPendingException();
		}
		this.connHandler = new ConnectionHandler(listener);
		acceptor.accept(null, this.connHandler);
	}
	
	//重启
	private synchronized void restart() throws IOException{
		if(group.isShutdown()){
			throw new IOException("group already shutdown..");
		}
		try{
			acceptor.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		acceptor = AsynchronousChannelProvider.provider().openAsynchronousServerSocketChannel(group);
		acceptor.bind(listenAddress, 1);
	}
	
	public synchronized void shutdown(){
		if(acceptor!=null && acceptor.isOpen()){
			try{
				acceptor.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		if(group!=null && !group.isShutdown()){
			boolean groupHasShutDown = false;
			try {
				groupHasShutDown = group.awaitTermination(1L, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			if(!groupHasShutDown){
				try {
					group.shutdownNow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel,Void>{
		
		private final ConnectionListener listener;
		
		public ConnectionHandler(ConnectionListener listener){
			this.listener = listener;
		}
		
		@Override
		public void completed(AsynchronousSocketChannel arg0, Void arg1) {
			// TODO Auto-generated method stub
			try {
				AsynchronousMessageChannel channel = new AsynchronousMessageChannel(
						arg0);
				if (this.listener != null) {
					this.listener.newConnection(channel);
				}
				acceptor.accept(null, this);
			} catch (Throwable t) {
				System.out.println("error happend...");
				t.printStackTrace();
				failed(t, null);
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			if(arg0 instanceof CancellationException){
				return;
			}
//			arg0.printStackTrace();
			try{
				restart();
				acceptor.accept(null,this);//当自己被关掉时 抛出一个 group already shutdown..
			}catch(IOException ex){
//				ex.printStackTrace();
				if(this.listener!=null){
					this.listener.shutdown();
				}
				shutdown();
			}
		}
	}
}
