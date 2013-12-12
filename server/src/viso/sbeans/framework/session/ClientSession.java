package viso.sbeans.framework.session;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSession {

	ClientSessionService service;
	
	public ClientSession(ClientSessionService service){
		this.service = service;
	}
	
	interface SessionEvent {
		public void onServiceEvent(ClientSessionHandler sessionProtocol,
				ClientSessionService service, BigInteger sessionRefId);// 进行某个事件操作
	}

	public static ConcurrentHashMap<BigInteger,EventQueue> stores
		= new ConcurrentHashMap<BigInteger,EventQueue>();
	
	public void putEvent(BigInteger sessionRefId, SessionEvent event){
		EventQueue queue = stores.get(sessionRefId);
		if(queue==null){
			queue = new EventQueue(sessionRefId);
			stores.put(sessionRefId, queue);
		}
		queue.putEvent(event);
	}
	
	class EventQueue{
		
		BigInteger sessionRefId;
		
		BlockingQueue<SessionEvent> eventQueue = new LinkedBlockingQueue<SessionEvent>();
		
		public EventQueue(BigInteger sessionRefId){
			this.sessionRefId = sessionRefId;
		}
		
		/**
		 * 添加一个事件
		 * */
		public void putEvent(SessionEvent event){
			eventQueue.offer(event);
		}
		
		/**
		 * 执行一部分事件
		 * */
		public void doSomeEvents(){
			if(eventQueue.isEmpty()) return;//nothing need todo;
			for(int i=0;i<20;i++){
				//一次执行一些事件
				if(eventQueue.isEmpty())return;
				SessionEvent event = eventQueue.poll();
				event.onServiceEvent(service.getClientSessionHandler(sessionRefId), service, sessionRefId);
			}
			if(eventQueue.isEmpty()) return;
			//等待下一次调度
			service.onServiceEventQueue(this,sessionRefId);
		}
	}
	
	/**
	 * 发送一则消息
	 */
	class SendEvent implements SessionEvent {

		byte[] message;

		public SendEvent(byte[] message) {
			this.message = message;
		}

		@Override
		public void onServiceEvent(ClientSessionHandler sessionProtocol,
				ClientSessionService service, BigInteger sessionRefId) {
			// TODO Auto-generated method stub
			service.commitAction(sessionProtocol.new SendMessageAction(this),
					sessionRefId);
		}
	}
}
