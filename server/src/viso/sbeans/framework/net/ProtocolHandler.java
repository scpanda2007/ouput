package viso.sbeans.framework.net;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadPendingException;

import viso.sbeans.framework.protocol.ProtocolHeader;
import viso.sbeans.framework.protocol.RequestCompletion;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;
import viso.sbeans.framework.protocol.SessionProtocolHandler;

/**底层网络协议处理器*/
public class ProtocolHandler {
	
	AsynchronousMessageChannel channel;
	SessionProtocolAcceptor sessionAcceptor;
	SessionProtocolHandler sessionHandler;
	ProtocolAcceptor acceptor;
	
	private ConnectionReadHandler readHandler = new ConnectionReadHandler();
	
	public ProtocolHandler(ProtocolAcceptor acceptor, SessionProtocolAcceptor listener, 
			AsynchronousMessageChannel channel){
		this.channel = channel;
		this.sessionAcceptor = listener;
		this.acceptor = acceptor;
		scheduelRead();
	}
	
	public void scheduelRead(){
		this.acceptor.scheduledNonTransactionTask(new Runnable(){
			@Override
			public void run(){
				// TODO Auto-generated method stub
				readNow();
			}
		});
	}
	
	public void readNow(){
		if(isOpen()){
			readHandler.read();
		}else{
			close();
		}
	}
	
	public boolean isOpen(){
		return this.channel.isOpen();
	}
	
	public void close() {
		if (isOpen()) {
			try {
				this.channel.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	void handleReceivedMessage(int opcode, MessageBuffer message) {
		switch (opcode) {
		case ProtocolHeader.kLogin: {
			sessionAcceptor.LoginNow(this, new LoginComplete());
			readNow();//立即读取数据，避免收到早期的信息
		}
			;
			break;
		case ProtocolHeader.kSessionMsg: {
			if (sessionHandler != null) {
				sessionHandler.handleSessionMessage(message,
						new SessionMessageComplete());
			}else{
				//drop the message. and no need to read..
			}
		}
			;
			break;
		}
	}
	
	private class ConnectionReadHandler implements CompletionHandler<MessageBuffer,Void>{
		
		private Object readLock = new Object();
		private boolean isReading = false;
		
		public void read(){
			synchronized(readLock){
				if(isReading){
					throw new ReadPendingException();
				}
				isReading = true;
			}
			channel.read(this);
		}

		@Override
		public void completed(MessageBuffer arg0, Void arg1) {
			// TODO Auto-generated method stub
			synchronized(readLock){
				isReading = false;
			}
			try{
				handleReceivedMessage(arg0.readByte(),arg0);
			}catch(Throwable t){
				failed(t,null);
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			close();//出现异常后关闭
		}
	}
	
	private class LoginComplete implements RequestCompletion<SessionProtocolHandler>{

		@Override
		public void completed(SessionProtocolHandler handler) {
			// TODO Auto-generated method stub
			sessionHandler = handler;
		}
		
	}
	
	private class SessionMessageComplete implements RequestCompletion<Void>{

		@Override
		public void completed(Void result) {
			// TODO Auto-generated method stub
			readHandler.read();
		}
		
	}
}
