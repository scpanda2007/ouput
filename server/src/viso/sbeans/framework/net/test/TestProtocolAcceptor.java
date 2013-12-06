package viso.sbeans.framework.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class TestProtocolAcceptor {
	
	DummyServer server;
	InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1",12345);
	ArrayList<DummyClient> clients;
	
	@Before
	public void setUp(){
		server = new DummyServer("127.0.0.1",12345,"txt");
		server.startService();
		clients = new ArrayList<DummyClient>();
	}
	
	@After
	public void tearDown(){
		for(DummyClient client : clients){
			client.shutdown();
		}
		server.shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAccept(){
		server.accept();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testConn(){
		server.accept();
		createTestClient(1);
		DummyClient client = clients.get(0);
		client.connectAndWait(serverAddress);
	}
	
	@Test
	public void testLogin(){
		createTestClient(1);
		DummyClient client = clients.get(0);
		server.accept();
		client.connectAndWait(serverAddress);
		try {
			client.writeLogin().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRecieveMessage(){
		createTestClient(1);
		DummyClient client = clients.get(0);
		server.accept();
		client.connectAndWait(serverAddress);
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
		server.accept();
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
		server.accept();
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
		
		ArrayList<Future<Void>> waitLogin = new ArrayList<Future<Void>>();
		for (DummyClient client : clients) {
			waitLogin.add(client.writeLogin());
		}
		
		try {
			for (Future<Void> wait : waitLogin) {
				wait.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int count=0;
		for (DummyClient client : clients) {
			client.writeProtocolMessage("hello"+"["+(count++)+"]", 1);
		}
		
		try {
			Thread.sleep(10000);
			System.out.println("-----------------------------------");
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
}
