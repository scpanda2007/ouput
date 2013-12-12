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
				ClientSessionService service, BigInteger sessionRefId);// ����ĳ���¼�����
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
		 * ���һ���¼�
		 * */
		public void putEvent(SessionEvent event){
			eventQueue.offer(event);
		}
		
		/**
		 * ִ��һ�����¼�
		 * */
		public void doSomeEvents(){
			if(eventQueue.isEmpty()) return;//nothing need todo;
			for(int i=0;i<20;i++){
				//һ��ִ��һЩ�¼�
				if(eventQueue.isEmpty())return;
				SessionEvent event = eventQueue.poll();
				event.onServiceEvent(service.getClientSessionHandler(sessionRefId), service, sessionRefId);
			}
			if(eventQueue.isEmpty()) return;
			//�ȴ���һ�ε���
			service.onServiceEventQueue(this,sessionRefId);
		}
	}
	
	/**
	 * ����һ����Ϣ
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
