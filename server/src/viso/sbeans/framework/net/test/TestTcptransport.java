package viso.sbeans.framework.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import viso.sbeans.framework.net.AsynchronousMessageChannel;
import viso.sbeans.framework.net.ConnectionListener;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.net.TcpTransport;
import viso.com.util.NamedThreadFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

public class TestTcptransport {
	
	TestServer server;
	InetSocketAddress serverAddress;
	ArrayList<TestClient> clients;
	
	
	@Before
	public void setUp(){
		server = new TestServer("server","127.0.0.1", 12345);
		serverAddress = new InetSocketAddress("127.0.0.1",12345);
		clients = new ArrayList<TestClient>();
	}
	
	
	@After
	public void tearDown(){
		for(TestClient client : clients){
			client.shutdown();
		}
		server.shutdownMe();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createTestClient(int number){
		for(int i=0;i<number;i+=1){
			TestClient client = new TestClient("client["+i+"]");
			clients.add(client);
		}
	}
	
	@Test
	public void testAccept(){
		createTestClient(1);
		TestClient client = clients.get(0);
		server.startService();
		client.connectAndWait(serverAddress);
	}
	
	@Test
	public void testRecieveMessage(){
		createTestClient(1);
		TestClient client = clients.get(0);
		server.startService();
		client.connectAndWait(serverAddress);
		client.writeMessage("client["+0+"] start send mssage--", 100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMulti2One() {
		createTestClient(1000);
		server.startService();
		ArrayList<Future<Void>> waitConnects = new ArrayList<Future<Void>>();
		try {
			for (TestClient client : clients) {
				waitConnects.add(client.connectNoWait(serverAddress));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			for (Future<Void> wait : waitConnects) {
				wait.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (TestClient client : clients) {
			client.writeMessage("client["+0+"] start send mssage--", 100);
		}
		
		try {
			Thread.sleep(10000);
			System.out.println("-----------------------------------");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class TestServer implements ConnectionListener{
		TcpTransport transport;
		String name;
		
		TestServer(String name,String hostname,int port){
			this.name = name;
			transport = new TcpTransport(hostname,port);
		}
		
		@Override
		public void newConnection(AsynchronousMessageChannel channel)
				throws Exception {
			// TODO Auto-generated method stub
			System.out.println("receive a new channel");
			channel.read(new ServerReader(this,channel));
		}
		
		public void putMessage(MessageBuffer buffer){
			this.messages.add(buffer);
		}
		
		@Override
		public void shutdown() {
			// TODO Auto-generated method stub
			running = false;
			executor.shutdown();
		}
		
		public void shutdownMe() {
			running = false;
			transport.shutdown();
			if (executor == null || executor.isShutdown())
				return;
			executor.shutdown();
		}
		
		BlockingQueue<MessageBuffer> messages = new LinkedBlockingQueue<MessageBuffer>();
		
		ExecutorService executor;
		
		private volatile boolean running;
		
		public void startService(){
			transport.accept(this);
			executor = Executors.newFixedThreadPool(4, new NamedThreadFactory("testerServer:"+name+""));
			running = true;
			for(int i=0;i<4;i++){
				executor.submit(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MessageBuffer buffer;
						while (running) {
							try {
								buffer = messages.take();
								System.out.println("[" + name
										+ "] rece a message :: " + System.currentTimeMillis() 
										+ buffer.readUTF());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
				});
			}
		}
	}
	
	private class ServerReader implements CompletionHandler<MessageBuffer,Void>{

		TestServer server;
		
		private final AsynchronousMessageChannel channel;
		
		ServerReader(TestServer server,AsynchronousMessageChannel channel){
			this.server = server;
			this.channel = channel;
		}
		
		@Override
		public void completed(MessageBuffer arg0, Void arg1) {
			// TODO Auto-generated method stub
			server.putMessage(arg0);
			this.channel.read(this);
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class TestClient{
		AsynchronousSocketChannel orgchannel;
		AsynchronousMessageChannel channel;
		String name;
		TestClient(String name){
			this.name = name;
		}
		
		public Future<Void> writeMessage(String message,int max){
			MessageBuffer sendBuffer = new MessageBuffer(1024);
			sendBuffer.writeUTF(message);
			return channel.write(sendBuffer.flip().buffer(), new ClientWriter(this,max));
		}
		
		public Future<Void> writeMessage(String message,ClientWriter writer){
			MessageBuffer sendBuffer = new MessageBuffer(1024);
			sendBuffer.writeUTF(message);
			return channel.write(sendBuffer.flip().buffer(), writer);
		}
		
		public Future<Void> writeMessage(String message){
			MessageBuffer sendBuffer = new MessageBuffer(1024);
			sendBuffer.writeUTF(message);
			return channel.write(sendBuffer.flip().buffer(), null);
		}
		
		public void shutdown(){
			if(this.channel!=null && this.channel.isOpen()){
				try {
					this.channel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void connectAndWait(InetSocketAddress end){
			try {
				this.orgchannel = AsynchronousSocketChannel.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				this.orgchannel.connect(end).get();
				this.channel = new AsynchronousMessageChannel(this.orgchannel);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.channel = new AsynchronousMessageChannel(this.orgchannel);
		}
		
		public Future<Void> connectNoWait(InetSocketAddress end) throws IOException{
			this.orgchannel = AsynchronousSocketChannel.open();
			this.channel = new AsynchronousMessageChannel(this.orgchannel);
			return this.orgchannel.connect(end);
		}
	}
	
	private class ClientWriter implements CompletionHandler<Void,Void>{
		
		TestClient client;
		int max;
		int count;
		public ClientWriter(TestClient client,int max){
			this.client = client;
			this.max = max;
			this.count = 0;
		}
		
		@Override
		public void completed(Void arg0, Void arg1) {
			// TODO Auto-generated method stub
			if(this.count < max){
				this.count += 1;
				String msg = "["+client.name+"] hello this my "+count+"rd mssage-"+System.currentTimeMillis();
//				System.out.println("send : "+msg);
				client.writeMessage(msg,this);
			}
		}
		
		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
		}
		
	}
}
