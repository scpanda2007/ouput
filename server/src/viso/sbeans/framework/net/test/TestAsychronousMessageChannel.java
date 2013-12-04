package viso.sbeans.framework.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.*;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.net.AsynchronousMessageChannel;
import viso.sbeans.framework.net.MessageBuffer;

public class TestAsychronousMessageChannel {
	
	TestServer server0;
	TestServer server1;

	@Before
	public void setUp() {
		server0 = new TestServer("127.0.0.1",12345,"server0");
		server1 = new TestServer("127.0.0.1",12346,"server1");
	}
	
	@After
	public void tearDown(){
		if(server0!=null)server0.shutDown();
		if(server1!=null)server1.shutDown();
	}
	
	@Test
	public void testAccept(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
	}
	
	@Test
	public void testConnect(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
		server0.connect(server1.end);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWriteAndRece(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
		server1.startServer();
		server0.connect(server1.end);
		server0.writeMessage("hello this is server0");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWriteAndReceALot(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
		server1.startServer();
		server0.connect(server1.end);
		try {
			for (int i = 0; i < 1000; i += 1) {
				server0.writeMessage("hello this is server0"+System.currentTimeMillis()).get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private class TestServer {
		
		AsynchronousServerSocketChannel serverChannel;
		AsynchronousSocketChannel clientChannel;
		AsynchronousMessageChannel messageChannel;
		InetSocketAddress end;
		AsynchronousChannelGroup group;
		
		private final String serverName;
		
		public TestServer(String hostname, int port, String serverName) {
			this.serverName = serverName;
			end = new InetSocketAddress(hostname, port);
			try {
				group = AsynchronousChannelProvider
						.provider()
						.openAsynchronousChannelGroup(
								Executors.newCachedThreadPool(new NamedThreadFactory(
												"TestServer:" + serverName)), 1);
				serverChannel = AsynchronousServerSocketChannel.open(group);
				serverChannel.bind(end,1);
				clientChannel = AsynchronousSocketChannel.open(group);
				executor = Executors.newScheduledThreadPool(
						1, new NamedThreadFactory("ConnHandler:" + serverName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private ExecutorService executor;
		
		private boolean running = false;
		
		public void startServer(){
			
			running = true;
			
			executor.submit(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (running) {
						MessageBuffer message;
						try {
							message = messages.take();
							System.out.println("[Message Handler] "+System.currentTimeMillis() + ":" + serverName + " read a message "
									+ message.readUTF());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			});
		}
		
		public void shutDown(){
			if (messageChannel != null && messageChannel.isOpen()) {
				try {
					messageChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				if(serverChannel.isOpen())serverChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(group!=null && !group.isShutdown()){
				try {
					group.awaitTermination(1L, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
				try {
					group.shutdownNow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			running = false;
		}
		
		public void accept(CompletionHandler<AsynchronousSocketChannel,TestServer> handler){
			serverChannel.accept(this, handler);
		}
		
		public void connect(InetSocketAddress endpoint){
			try {
				clientChannel.connect(endpoint).get();
				messageChannel = new AsynchronousMessageChannel(clientChannel);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Future<Void> writeMessage(String message){
			MessageBuffer buffer = new MessageBuffer(1024);
			buffer.writeUTF(message);
			return messageChannel.write(buffer.flip().buffer(), null);
		}
		
		//////////////////////////////////////////////////////////////////////
		
		Map<Integer,Client> clients = new ConcurrentHashMap<Integer,Client>();
		
		AtomicInteger seq = new AtomicInteger(0);
		
		public void newConnection(final AsynchronousMessageChannel channel){
			
			int id = seq.getAndIncrement();
			clients.put(id, new Client(id,channel));
			SessionMessageHandler handler = new SessionMessageHandler(channel,this);
			channel.read(handler);
		}
		
		BlockingQueue<MessageBuffer> messages = new LinkedBlockingQueue<MessageBuffer>();
		
		public void putMessage(MessageBuffer message){
			messages.add(message);
		}
	}
	
	private class SessionMessageHandler implements CompletionHandler<MessageBuffer, Void>{

		final AsynchronousMessageChannel channel;
		
		final TestServer server;
		
		SessionMessageHandler(final AsynchronousMessageChannel channel,TestServer server){
			this.channel = channel;
			this.server = server;
		}
		
		@Override
		public void completed(MessageBuffer result, Void attachment) {
			// TODO Auto-generated method stub
			this.server.putMessage(result);
			channel.read(this);
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class Client{
		@SuppressWarnings("unused")
		AsynchronousMessageChannel channel;
		@SuppressWarnings("unused")
		int id;
		public Client(int id, AsynchronousMessageChannel channel){
			this.channel = channel;
			this.id = id;
		}
	}
	
	private class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel,TestServer>{

		@Override
		public void completed(AsynchronousSocketChannel arg0, TestServer server) {
			// TODO Auto-generated method stub
			try {
				System.out.println(server.serverName+" rece a new connection "+arg0.getRemoteAddress().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AsynchronousMessageChannel channel = new AsynchronousMessageChannel(arg0);
			server.newConnection(channel);
		}

		@Override
		public void failed(Throwable arg0, TestServer server) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
