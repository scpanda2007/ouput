package viso.sbeans.framework.net.test;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.net.ProtocolAcceptor;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.net.TcpTransport;
import viso.sbeans.framework.protocol.RequestCompletion;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;
import viso.sbeans.framework.protocol.SessionProtocolHandler;

import static viso.com.util.Objects.checkNull;

public class DummyServer {
	
	String name;
	ProtocolAcceptor acceptor;
	ExecutorService executor;
	ProtocolHandler proHandler;
	
	public DummyServer(String host,int port, String name){
		checkNull("name",name);
		Properties property = new Properties();
		property.setProperty(TcpTransport.ADD_HOST, host);
		property.setProperty(TcpTransport.ADD_PORT, new Integer(port).toString());
		acceptor = new ProtocolAcceptor(property);
		executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("DummyServer:"+name));
	}
	
	public void accept(){
		acceptor.accept(new DummySessionAcceptor());
	}
	
	private BlockingQueue<MessageBuffer> messages = new LinkedBlockingQueue<MessageBuffer>();
	
	private class DummySessionAcceptor implements SessionProtocolAcceptor{
		
		AtomicInteger counter = new AtomicInteger(0);
		
		@Override
		public void LoginNow(ProtocolHandler handler,
				RequestCompletion<SessionProtocolHandler> request) {
			// TODO Auto-generated method stub
			proHandler = handler;
			System.out.println("["+System.currentTimeMillis()+"] rece a login request "+counter.getAndIncrement());
			request.completed(new DummySessionHandler());
		}
		
	}
	
	private class DummySessionHandler implements SessionProtocolHandler{

		@Override
		public void handleSessionMessage(MessageBuffer message,
				RequestCompletion<Void> handler) {
			// TODO Auto-generated method stub
			try {
				messages.put(message);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.completed(null);
		}
	}
	
	public void startService(){
		if(running){
			throw new IllegalStateException(" already run. ");
		}
		running = true;
		executor.submit(new MessageConsumer());
	}
	
	private class MessageConsumer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(running){
				try {
					MessageBuffer message = messages.take();
					System.out.println("rece a message : "+message.readUTF());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private volatile boolean running;
	
	public void shutdown(){
		running = false;
		acceptor.close();
		if(executor==null && executor.isShutdown()) return;
		boolean hasShutDown = false;
		try {
			hasShutDown = executor.awaitTermination(1L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!hasShutDown){
			executor.shutdownNow();
		}
	}
}
