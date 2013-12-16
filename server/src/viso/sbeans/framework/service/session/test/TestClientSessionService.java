package viso.sbeans.framework.service.session.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Properties;

import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.net.TcpTransport;
import viso.sbeans.framework.net.test.DummyClient;
import viso.sbeans.framework.service.session.ClientSessionService;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.junit.BeforeClass;
import org.junit.AfterClass;

public class TestClientSessionService {
	static ClientSessionService service;
	static InetSocketAddress end;
	static Properties property;
	
	ArrayList<DummyClient> clients;
	DummyApp app = new DummyApp();
	
	@BeforeClass
	public static void beforeAll(){
		String host = "192.168.1.102";
		int port = 8000;
		end = new InetSocketAddress(host,port);
		property = new Properties();
		property.setProperty(TcpTransport.ADD_HOST, host);
		property.setProperty(TcpTransport.ADD_PORT, new Integer(port).toString());
	}
	
	@AfterClass
	public static void afterAll(){
		
	}
	
	@Before
	public void setUp(){
		clients = new ArrayList<DummyClient>();
		ClientSessionService.startClientSessionService(property);
		service = ClientSessionService.getInstance();
	}
	
	@After
	public void tearDown(){
		if(service==null)return;
		ClientSessionService closeService = service;
		service = null;
		for(DummyClient client : clients){
			client.shutdown();
		}
		closeService.shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNoStop(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			String cmd;
			do{
				cmd = reader.readLine();
			}while(cmd!=null && cmd.equals("a"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAccept(){
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testReceMsg(){
		createTestClient(1);
		DummyClient client = clients.get(0);
		client.connectAndWait(end);
		client.writeLogin();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start time::"+System.currentTimeMillis());
		client.writeProtocolMessage("hello this is testReceMsg",300000);
		System.out.println("end time::"+System.currentTimeMillis());
		//测试结果 16秒 单客户端 发送 300,000条信息,每条打印大约18750条/秒
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMontReceMsg(){
		createTestClient(3000);
		for (int i = 0; i < 3000; i++) {
			DummyClient client = clients.get(i);
			client.connectAndWait(end);
			client.writeLogin();
		}
		// 测试结果 4 秒
		try {
			Thread.sleep(2000);//
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start time::"+System.currentTimeMillis());
		for(int i=0;i<3000;i++){
			clients.get(i).writeProtocolMessage("hello this is testReceMsg",1000);
		}
		System.out.println("end time::"+System.currentTimeMillis());
		// 测试结果 发送字符串开始 60 秒内 185条[平均每个客户端处理数]*3000[并发客户端数]接收 并逐条打印 ，平均每秒处理 9250条
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testConcurrentReceMsg(){
		createTestClient(5000);
		TaskScheduler scheduler = new TaskScheduler();
		for (int i = 0; i < 5000; i++) {
			DummyClient client = clients.get(i);
			client.connectAndWait(end);
			client.writeLogin();
		}
		// 测试结果 4 秒
		try {
			Thread.sleep(3000);//
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start time::"+System.currentTimeMillis());
		for(int i=0;i<5000;i++){
			final DummyClient client = clients.get(i);
			scheduler.sumbit(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					client.writeProtocolMessage("hello this is testReceMsg",100);
				}
			});
		}
		System.out.println("end time::"+System.currentTimeMillis());
		// 测试结果 发送字符串开始 32 秒内 100条[平均每个客户端处理数]*5000[并发客户端数]接收 并逐条打印 ，平均每秒处理 15625条
		// 第100条处理时间 1386919081453 - - 1386919081595
		// cpu 前15秒 96%，后15秒55%
		// 内存 0.5GB
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduler.shutdown();
	}
	
	public void createTestClient(int number){
		for(int i=0;i<number;i+=1){
			DummyClient client = new DummyClient("client["+i+"]");
			clients.add(client);
		}
	}
	
}
