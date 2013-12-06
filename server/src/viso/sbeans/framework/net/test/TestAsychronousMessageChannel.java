package viso.sbeans.framework.net.test;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.ArrayList;
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
		if(server0!=null){
			server0.shutDown();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(server1!=null){
			server1.shutDown();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	private class WriteMessageHandler implements CompletionHandler<Void,Void>{
		
		TestServer server;
		AsynchronousMessageChannel channel;
		int count;
		int max;
		public WriteMessageHandler(TestServer server,AsynchronousMessageChannel channel,int max){
			this.max = max;
			this.count = 0;
			this.server = server;
			this.channel = channel;
		}
		
		@Override
		public void completed(Void arg0, Void arg1) {
			// TODO Auto-generated method stub
			if(this.count<max){
				String hello = "hello this is server0"+"["+this.count+"]"+System.currentTimeMillis();
//				System.out.println("----"+hello);
				this.count+=1;
				server.writeMessageBig(hello);
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Test
	public void testWriteAndReceMont(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
		server1.startServer();
		server0.connect(server1.end);
		String hello = "hello this is server0"+"[init]"+System.currentTimeMillis();
		System.out.println("----"+hello);
		server0.writeMessage(hello,1000);//TODO: as a fact you should put this in completion handler;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMulti2One() {
		server1.accept(new DelayedConnectionHandler());
		server1.startServer();
		ArrayList<TestServer> clients = new ArrayList<TestServer>();
		ArrayList<Future<Void>> waits = new ArrayList<Future<Void>>();
		try {
			
			for (int i = 0; i < 1000; i += 1) {
				TestServer client = new TestServer("client" + i);
				clients.add(client);
				waits.add(client.connectNoWait(server1.end));
			}
			
			for(Future<Void> wait: waits){
				wait.get();
			}
			
			for (int i = 0; i < 1000; i += 1) {
				TestServer client = clients.get(i);
				String hello = "hello this is client" + "[" + i + "]"
				+ System.currentTimeMillis();
				System.out.println("----" + hello);
				client.writeMessageBig(hello,1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.err.println("close clients.........####################################");
			Thread.sleep(20000);
			for (TestServer client : clients) {
				client.shutDown();//同时关闭会产生大量的thread created in the group.
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWriteAndReceMontWithDelay(){
		server0.accept(new ConnectionHandler());
		server1.accept(new ConnectionHandler());
		server1.startServer();
		server0.connect(server1.end);
		String hello = "hello this is server0"+"[init]"+System.currentTimeMillis();
		System.out.println("----"+hello);
		server0.writeMessage(hello, 200);
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
		server1.accept(new DelayedConnectionHandler());
		server1.startServer();
		server0.connect(server1.end);
		String hello = "hello this is server0"+"[init]"+System.currentTimeMillis();
		System.out.println("----"+hello);
		server0.writeMessageBig(hello, 10);
		try {
			Thread.sleep(1000);//2000 下会收到到一个-1,但是1000却没有
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server0.shutDown();
		try {
			Thread.sleep(10000);
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
		
		public TestServer(String serverName){
			this.serverName = serverName;
			try {
				clientChannel = AsynchronousSocketChannel.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
				serverChannel.bind(end,10);
//				serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 32 * 1024);
				clientChannel = AsynchronousSocketChannel.open(group);
				executor = Executors.newFixedThreadPool(2, new NamedThreadFactory("ConnHandler:" + serverName)); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private ExecutorService executor;
		
		private volatile boolean running = false;
		
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
			if (messageChannel!= null && messageChannel.isOpen()) {
				try {
					messageChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				if(serverChannel!=null && serverChannel.isOpen()){
					serverChannel.close();
				}
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
		
		public Future<Void> connectNoWait(InetSocketAddress endpoint){
			messageChannel = new AsynchronousMessageChannel(clientChannel);
			return clientChannel.connect(endpoint);
		}
		
		public void connect(InetSocketAddress endpoint){
			try {
				clientChannel.connect(endpoint).get();
				messageChannel = new AsynchronousMessageChannel(clientChannel);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e){
				e.printStackTrace();
			}
		}
		
		WriteMessageHandler handler;
		
		public Future<Void> writeMessageBig(String message, int max){
			MessageBuffer buffer = new MessageBuffer(1024*4);
			buffer.writeUTF(message);
			buffer.writeBytes(new byte[1024*3]);
			handler = new WriteMessageHandler(this,messageChannel,max);
			return messageChannel.write(buffer.flip().buffer(), handler);
		}
		
		public Future<Void> writeMessage(String message, int max){
			MessageBuffer buffer = new MessageBuffer(1024);
			buffer.writeUTF(message);
			handler = new WriteMessageHandler(this,messageChannel,max);
			return messageChannel.write(buffer.flip().buffer(), handler);
		}
		
		public Future<Void> writeMessageBig(String message){
			MessageBuffer buffer = new MessageBuffer(1024*4);
			buffer.writeUTF(""+serverName+":"+message);
			buffer.writeBytes(new byte[1024*3]);
			return messageChannel.write(buffer.flip().buffer(), handler);
		}
		
		public Future<Void> writeMessage(String message){
			MessageBuffer buffer = new MessageBuffer(1024);
			buffer.writeUTF(message);
			return messageChannel.write(buffer.flip().buffer(), handler);
		}
		
		//////////////////////////////////////////////////////////////////////
		
		Map<Integer,Client> clients = new ConcurrentHashMap<Integer,Client>();
		
		AtomicInteger seq = new AtomicInteger(0);
		
		public void newConnectionWithDelayReader(final AsynchronousMessageChannel channel){
			int id = seq.getAndIncrement();
			clients.put(id, new Client(id,channel));
			SessionMessageHandlerDelayed handler = new SessionMessageHandlerDelayed(channel,this);
			channel.read(handler);
		}
		
		public void newConnection(final AsynchronousMessageChannel channel){
			
			int id = seq.getAndIncrement();
			clients.put(id, new Client(id,channel));
			SessionMessageHandler handler = new SessionMessageHandler(channel,this);
			channel.read(handler);
		}
		
		BlockingQueue<MessageBuffer> messages = new LinkedBlockingQueue<MessageBuffer>();
		
		public void putMessage(MessageBuffer message){
			if(message!=null){
				System.out.println("put message..");
			}
			messages.add(message);
		}
	}
	
	AtomicInteger count = new AtomicInteger(0);
	
	private class SessionMessageHandlerDelayed extends SessionMessageHandler{

		SessionMessageHandlerDelayed(AsynchronousMessageChannel channel,
				TestServer server) {
			super(channel, server);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void completed(MessageBuffer result, Void attachment) {
			// TODO Auto-generated method stub
			this.server.putMessage(result);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}//test 
			channel.read(this);
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
			if (exc instanceof EOFException) {
				try {
					if(channel!=null && channel.isOpen())channel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				exc.printStackTrace();
			}
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
	
	private class DelayedConnectionHandler extends ConnectionHandler{
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
			server.newConnectionWithDelayReader(channel);
			server.accept(this);
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
			server.accept(this);
		}

		@Override
		public void failed(Throwable arg0, TestServer server) {
			// TODO Auto-generated method stub
		}
		
	}
	
}
