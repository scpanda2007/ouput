package viso.sbeans.framework.service.session;

import java.math.BigInteger;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.net.ProtocolAcceptor;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;
import viso.sbeans.framework.protocol.SessionProtocolHandler;

public class ClientSessionService {
	
	TaskScheduler scheduler;
	
	ProtocolAcceptor acceptor;
	
	private ConcurrentHashMap<BigInteger,ClientSessionHandler> handlers =
		new ConcurrentHashMap<BigInteger,ClientSessionHandler>();
	
	private static ClientSessionService sessionService;
	
	private AtomicInteger counter = new AtomicInteger(0);
	
	public static ClientSessionService getInstance(){
		return sessionService;
	}
	
	public static void startClientSessionService(Properties property){
		if(sessionService!=null){
			System.out.println(" Already start a client session. Now i will shut it down");
			sessionService.shutdown();//TODO: what should i do?
		}
		sessionService = new ClientSessionService(property);
	}
	
	private ClientSessionService(Properties property){
		scheduler = new TaskScheduler();
		acceptor = new ProtocolAcceptor(property);
		acceptor.accept(new SessionProtocolAcceptorImpl());
	}
	
	public void startService(){
	}
	
	public ClientSessionHandler getSessionHandler(BigInteger sessionRefId){
		return handlers.get(sessionRefId);
	}
	
	public TaskScheduler getTaskScheduler(){
		return scheduler;
	}
	
	public void shutdown(){
		acceptor.close();
		scheduler.shutdown();
	}
	
	private class SessionProtocolAcceptorImpl implements SessionProtocolAcceptor{

		@Override
		public void LoginNow(ProtocolHandler proHandler,
				RequestCompletionHandler<SessionProtocolHandler> request) {
			// TODO Auto-generated method stub
			System.out.println("login new is called..");
			BigInteger sessionRefId = new BigInteger(""+counter.getAndIncrement());
			ClientSessionHandler handler = new ClientSessionHandler(sessionService,proHandler,sessionRefId);
			handler.setUpClientSession();
			handlers.put(sessionRefId, handler);
			handler.notifyRequestComplete(request, handler);
		}
		
	}
	
}
