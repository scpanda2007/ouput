package viso.sbeans.framework.session;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.net.ProtocolAcceptor;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;
import viso.sbeans.framework.protocol.SessionProtocolHandler;
import viso.sbeans.framework.session.ClientSession.EventQueue;
import viso.sbeans.framework.session.ClientSession.SessionEvent;
import viso.sbeans.framework.util.TaskQueue;

public class ClientSessionService {
	
	TaskScheduler scheduler;
	
	private ConcurrentHashMap<BigInteger,ClientSessionHandler> clients = 
		new ConcurrentHashMap<BigInteger,ClientSessionHandler>();
	
	private ConcurrentHashMap<BigInteger,CommitActions> commitActions = 
		new ConcurrentHashMap<BigInteger,CommitActions>();
	
	private ConcurrentHashMap<BigInteger,TaskQueue> taskQueues =
		new ConcurrentHashMap<BigInteger,TaskQueue>();
	
	ProtocolAcceptor acceptor;
	
	ServerSessionProtocolAcceptor sessionAcceptor;
	
	ClientSessionServer server = new ClientSessionServerImpl();
	
	AtomicInteger sessionId = new AtomicInteger(0);
	
	public ClientSessionService(Properties property){
		scheduler = new TaskScheduler();
		acceptor = new ProtocolAcceptor(property);
	}
	
	public void shutdown(){
		scheduler.shutdown();
		acceptor.close();
		sessionAcceptor = new ServerSessionProtocolAcceptor();
	}
	
	public void startService(){
		acceptor.accept(sessionAcceptor);
	}
	
	public ClientSessionHandler getClientSessionHandler(BigInteger sessionRefId){
		return clients.get(sessionRefId);
	}
	
	public void commitAction(Action action,BigInteger sessionRefId){
		CommitActions commitAction = commitActions.get(sessionRefId);
		if(!commitActions.containsKey(sessionRefId)){
			commitAction = commitActions.putIfAbsent(sessionRefId, new CommitActions());
		}
		commitAction.add(action);//May also throw NullPointerException,如何处理执行顺序问题
	}
	
	/**
	 * 执行某个事件队列
	 * */
	public void onServiceEventQueue(final EventQueue eventQueue, final BigInteger sessionRefId){
		TaskQueue queue = taskQueues.get(sessionRefId);
		if(queue==null)return;//hehe
		queue.submit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				eventQueue.doSomeEvents();
			}
		});
	}
	
	private class ServerSessionProtocolAcceptor implements SessionProtocolAcceptor{

		@Override
		public void LoginNow(ProtocolHandler proHandler,
				RequestCompletionHandler<SessionProtocolHandler> request) {
			// TODO Auto-generated method stub
			int session = sessionId.getAndIncrement();
			ClientSessionHandler clientHandler = new ClientSessionHandlerImpl(scheduler.createTaskQueue(),proHandler);
			clients.put(new BigInteger(""+session), clientHandler);
			request.completed(clientHandler);
		}
		
	}
	
	//
	private class ClientSessionHandlerImpl extends ClientSessionHandler{
		public ClientSessionHandlerImpl(TaskQueue taskQueue,ProtocolHandler handler){
			super(handler);
		}
	}
	
	//
	private class ClientSessionServerImpl implements ClientSessionServer{

		@Override
		public void send(byte[] sesseionidbytes, byte[] message) {
			// TODO Auto-generated method stub
			BigInteger id = new BigInteger(1,sesseionidbytes);
			ClientSessionHandler handler = clients.get(id);
			ClientSession session = new ClientSession(ClientSessionService.this);
			session.putEvent(id, new SendEvent(message));
		}
		
	}
	
	interface Action{
		void doAction();
	}
	
	class CommitActions extends LinkedList<Action>{
		
		private static final long serialVersionUID = 1L;

		public Action peekAction(){
			return this.peek();
		}
		
		public void commitAction(Action action){
			this.offer(action);
		}
		
	}
	
}
