package viso.sbeans.framework.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import viso.sbeans.framework.net.AsynchronousMessageChannel;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.net.TcpTransport;
import viso.sbeans.framework.net.TcpTransport.ConnectionListener;
import viso.com.util.NamedThreadFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTcptransport {
	
	TestServer server;
	InetSocketAddress serverAddress;
	ArrayList<DummyClient> clients;
	
	
	@Before
	public void setUp(){
		server = new TestServer("server","127.0.0.1", 12345);
		serverAddress = new InetSocketAddress("127.0.0.1",12345);
		clients = new ArrayList<DummyClient>();
	}
	
	
	@After
	public void tearDown(){
		for(DummyClient client : clients){
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
			DummyClient client = new DummyClient("client["+i+"]");
			clients.add(client);
		}
	}
	
	@Test
	public void testAccept(){
		createTestClient(1);
		DummyClient client = clients.get(0);
		server.startService();
		client.connectAndWait(serverAddress);
	}
	
	@Test
	public void testRecieveMessage(){
		createTestClient(1);
		DummyClient client = clients.get(0);
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
	public void testMultiConnect(){
		createTestClient(1000);
		server.startService();
		ArrayList<Future<Void>> waitConnects = new ArrayList<Future<Void>>();
		try {
			for (DummyClient client : clients) {
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
	}
	
	@Test
	public void testMulti2One() {
		createTestClient(1000);
		server.startService();
		ArrayList<Future<Void>> waitConnects = new ArrayList<Future<Void>>();
		try {
			for (DummyClient client : clients) {
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
		
		for (DummyClient client : clients) {
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
			Properties property = new Properties();
			property.setProperty(TcpTransport.ADD_HOST, hostname);
			property.setProperty(TcpTransport.ADD_PORT, new Integer(port).toString());
			transport = new TcpTransport(property);
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
	
}
