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
import viso.sbeans.framework.util.TaskQueue;

public class ClientSessionService {
	//接收消息时的调度
	TaskScheduler receScheduler;
	//发起执行操作调度
	TaskScheduler actionScheduler;
	
	ProtocolAcceptor acceptor;
	
	private ConcurrentHashMap<BigInteger,ClientSessionHandler> handlers =
		new ConcurrentHashMap<BigInteger,ClientSessionHandler>();
	
	private ConcurrentHashMap<BigInteger,TaskQueue> actionTaskQueues =
		new ConcurrentHashMap<BigInteger,TaskQueue>();
	
	private static ClientSessionService sessionService;
	
	private AtomicInteger counter = new AtomicInteger(0);
	
	private ClientSessionServerImpl server;
	
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
		receScheduler = new TaskScheduler();
		actionScheduler = new TaskScheduler();
		acceptor = new ProtocolAcceptor(property);
		server = new ClientSessionServerImpl();
		acceptor.accept(new SessionProtocolAcceptorImpl());
	}
	
	public void startService(){
	}
	
	public ClientSessionHandler getSessionHandler(BigInteger sessionRefId){
		return handlers.get(sessionRefId);
	}
	
	public TaskScheduler getTaskScheduler(){
		return receScheduler;
	}
	
	public void shutdown(){
		acceptor.close();
		receScheduler.shutdown();
	}
	
	private class SessionProtocolAcceptorImpl implements SessionProtocolAcceptor{

		@Override
		public void LoginNow(ProtocolHandler proHandler,
				RequestCompletionHandler<SessionProtocolHandler> request) {
			// TODO Auto-generated method stub
			System.out.println("login new is called..");
			BigInteger sessionRefId = new BigInteger(""+counter.getAndIncrement());
			ClientSessionHandler handler = new ClientSessionHandler(sessionService,proHandler,sessionRefId);
			handlers.put(sessionRefId, handler);
			handler.setUpClientSession();
			handler.notifyRequestComplete(request, handler);
		}
		
	}
	
	public interface Action{
		public void doAction();
	}
	
	public void commitAction(final Action action, final BigInteger sessionRefId){
		TaskQueue actionQueue = actionTaskQueues.get(sessionRefId);
		if(actionQueue==null){
			actionQueue = actionScheduler.createTaskQueue();
			TaskQueue old = actionTaskQueues.putIfAbsent(sessionRefId, actionQueue);
			actionQueue = old==null? actionQueue : old;
		}
		actionQueue.submit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				action.doAction();
			}
		});
	}
	
	private class ClientSessionServerImpl implements ClientSessionServer{

		@Override
		public void send(byte message[], BigInteger sessionid) {
			// TODO Auto-generated method stub
			if(!handlers.containsKey(sessionid))return;
			ClientSessionHandler handler = handlers.get(sessionid);
			commitAction(handler.new SendMessageAction(message),sessionid);
		}
		
	}
	
	public ClientSessionServer getClientSessionServer(BigInteger sessionRefId){
		return server;
	}
	
}
