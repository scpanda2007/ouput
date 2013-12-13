package viso.sbeans.framework.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import viso.sbeans.framework.net.AsynchronousMessageChannel;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.protocol.ProtocolHeader;

public class DummyClient{
	
	AsynchronousSocketChannel orgchannel;
	AsynchronousMessageChannel channel;
	String name;
	public DummyClient(String name){
		this.name = name;
	}
	
	public Future<Void> writeProtocolMessage(String message,int max){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeByte(ProtocolHeader.kSessionMsg);
		sendBuffer.writeUTF(message);
		return channel.write(sendBuffer.flip().buffer(), new ClientProtocolWriter(this,max));
	}
	
	public Future<Void> writeProtocolMessage(String message){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeByte(ProtocolHeader.kSessionMsg);
		sendBuffer.writeUTF(message);
		return channel.write(sendBuffer.flip().buffer(), null);
	}
	
	public Future<Void> writeLogin(){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeByte(ProtocolHeader.kLogin);
		return channel.write(sendBuffer.flip().buffer(), null);
	}
	
	public Future<Void> writeMessage(String message, ClientProtocolWriter writer){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeByte(ProtocolHeader.kSessionMsg);
		sendBuffer.writeUTF(message);
		return channel.write(sendBuffer.flip().buffer(), writer);
	}
	
	public Future<Void> writeProtocolMessage(String message, ClientWriter writer){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeUTF(message);
		return channel.write(sendBuffer.flip().buffer(), writer);
	}
	
	public Future<Void> writeMessage(String message,int max){
		MessageBuffer sendBuffer = new MessageBuffer(1024);
		sendBuffer.writeUTF(message);
		return channel.write(sendBuffer.flip().buffer(), new ClientWriter(this,max));
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

class ClientProtocolWriter implements CompletionHandler<Void,Void>{
	DummyClient client;
	int max;
	int count;
	public ClientProtocolWriter(DummyClient client,int max){
		this.client = client;
		this.max = max;
		this.count = 0;
	}
	
	@Override
	public void completed(Void arg0, Void arg1) {
		// TODO Auto-generated method stub
		if(this.count < max){
			this.count += 1;
			String msg = "["+client.name+"] hello this my "+count+"rd mssage";
			client.writeMessage(msg,this);
		}
	}
	
	@Override
	public void failed(Throwable arg0, Void arg1) {
		// TODO Auto-generated method stub
	}
}

class ClientWriter implements CompletionHandler<Void,Void>{
	
	DummyClient client;
	int max;
	int count;
	public ClientWriter(DummyClient client,int max){
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
			client.writeProtocolMessage(msg,this);
		}
	}
	
	@Override
	public void failed(Throwable arg0, Void arg1) {
		// TODO Auto-generated method stub
	}
	
}
