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
		//���Խ�� 16�� ���ͻ��� ���� 300,000����Ϣ,ÿ����ӡ��Լ18750��/��
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
		// ���Խ�� 4 ��
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
		// ���Խ�� �����ַ�����ʼ 60 ���� 185��[ƽ��ÿ���ͻ��˴�����]*3000[�����ͻ�����]���� ��������ӡ ��ƽ��ÿ�봦�� 9250��
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
		// ���Խ�� 4 ��
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
		// ���Խ�� �����ַ�����ʼ 32 ���� 100��[ƽ��ÿ���ͻ��˴�����]*5000[�����ͻ�����]���� ��������ӡ ��ƽ��ÿ�봦�� 15625��
		// ��100������ʱ�� 1386919081453 - - 1386919081595
		// cpu ǰ15�� 96%����15��55%
		// �ڴ� 0.5GB
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
