package viso.game.framework.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import viso.com.util.NamedThreadFactory;
import viso.game.framework.util.MessageBuffer;

public class TestServer {
	
	private class ConnectionManager implements ConnectionHandler{

		@Override
		public void newChannel(AsycMessageChannel channel) {
			if(!running.get())return;
			// TODO Auto-generated method stub
			int seq = sequeece.getAndIncrement();
			clients.put(seq, new Client(seq,channel));
		}
		
	}
	
	private class Client{
		int id;
		final AsycMessageChannel channel;
		public Client(int id,AsycMessageChannel channel){
			this.channel = channel;
			this.id = id;
			channel.read(new CompletionHandler<ByteBuffer,Integer>(){

				@Override
				public void completed(ByteBuffer arg0, Integer arg1) {
					// TODO Auto-generated method stub
					MessageBuffer message = new MessageBuffer(arg0.array());
					try {
						messages.put(message);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Client.this.channel.read(this);
				}

				@Override
				public void failed(Throwable arg0, Integer arg1) {
					// TODO Auto-generated method stub
					try {
						Client.this.channel.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
		
	}
	
	Map<Integer,Client> clients = new ConcurrentHashMap<Integer,Client>();
	
	BlockingQueue<MessageBuffer> messages = new LinkedBlockingQueue<MessageBuffer>();
	
	AtomicInteger sequeece = new AtomicInteger(0);
	
	ConnectionManager manager;
	
	ProtocolAcceptor acceptor;
	
	ScheduledExecutorService excutor;
	
	private AtomicBoolean running = new AtomicBoolean(false);
	
	private AtomicInteger waitSize = new AtomicInteger(0);
	
	public TestServer(){
		manager = new ConnectionManager();
		acceptor = new ProtocolAcceptor(null);
		excutor = Executors.newScheduledThreadPool(4, new NamedThreadFactory("TestService-TaskHandler"));
		
		for(int i=0;i<4;i++){
			excutor.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					waitSize.getAndIncrement();
					while(running.get()){
						MessageBuffer message = messages.poll();
						int id = message.readInt();
						String words = "";
						try {
							words = message.readUTF();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						MessageBuffer broad = new MessageBuffer(1024);
						broad.writeInt(id);
						try {
							broad.writeUTF(words);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(Client client : clients.values()){
							if(client.id!=id){
								client.channel.write(broad.buffer(), null);
							}
						}
					}
					waitSize.getAndDecrement();
				}

			});
		}
	}
	
	public void start(){
		if(!running.compareAndSet(false, true))return;
		acceptor.accept(manager);
	}
	
	public void shutdown() throws InterruptedException{
		if(running.compareAndSet(true, false)){
			while(waitSize.get()>0){
				Thread.sleep(1000);
			}
		}
		shutdownNow();
	}
	
	public void shutdownNow(){
		for(Client client : clients.values()){
			if(client.channel.isOpen()){
				try {
					client.channel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
