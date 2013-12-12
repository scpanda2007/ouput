package viso.sbeans.framework.session;

import java.util.Properties;

import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.net.ProtocolAcceptor;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;
import viso.sbeans.framework.protocol.SessionProtocolHandler;
import viso.sbeans.framework.util.TaskQueue;

public class ClientSessionService {
	
	TaskScheduler scheduler;
	
	ProtocolAcceptor acceptor;
	
	private static ClientSessionService sessionService;
	
	public static ClientSessionService getInstance(){
		return sessionService;
	}
	
	public ClientSessionService(Properties property){
		scheduler = new TaskScheduler();
		acceptor = new ProtocolAcceptor(property);
		acceptor.accept(new SessionProtocolAcceptorImpl());
	}
	
	public TaskScheduler getTaskScheduler(){
		return scheduler;
	}
	
	private class SessionProtocolAcceptorImpl implements SessionProtocolAcceptor{

		@Override
		public void LoginNow(ProtocolHandler proHandler,
				RequestCompletionHandler<SessionProtocolHandler> request) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
