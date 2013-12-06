package viso.sbeans.framework.net.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;

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
	
	public void createTestClient(int number){
		for(int i=0;i<number;i+=1){
			DummyClient client = new DummyClient("client["+i+"]");
			clients.add(client);
		}
	}
}
