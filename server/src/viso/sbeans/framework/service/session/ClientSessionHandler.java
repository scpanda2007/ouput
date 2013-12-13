package viso.sbeans.framework.service.session;

import java.math.BigInteger;

import viso.sbeans.app.AppListener;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolHandler;
import viso.sbeans.framework.service.data.DataService;
import viso.sbeans.framework.util.TaskQueue;

public class ClientSessionHandler implements SessionProtocolHandler{

	TaskQueue taskQueue;
	final ClientSessionService sessionService;
	final ProtocolHandler protocol;
	final BigInteger sessionRefId;
	
	public ClientSessionHandler(ClientSessionService sessionService,ProtocolHandler protocol,BigInteger sessionRefId){
		this.protocol = protocol;
		this.sessionService = sessionService;
		this.sessionRefId = sessionRefId;
	}
	
	@Override
	public void handleSessionMessage(final MessageBuffer message,
			final RequestCompletionHandler<Void> handler) {
		// TODO Auto-generated method stub
		taskQueue.submit(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ClientSessionImpl session = ClientSessionImpl.getSession(sessionRefId);
				session.getListener().handleSessionMessage(message);
			}
			
		});
		notifyRequestComplete(handler, null);
	}
	
	public void setUpClientSession(){
		taskQueue = sessionService.getTaskScheduler().createTaskQueue();
		taskQueue.submit(new LoginTask());
	}

	public <T> void notifyRequestComplete(final RequestCompletionHandler<T> request,final T result){
		taskQueue.submit(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("request complete..");
				request.completed(result);
			}
			
		});
	}
	
	private class LoginTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			AppListener app = DataService.getInstance().getService("viso.sbeans.app.AppListener",AppListener.class);
			if(app==null){
				throw new NullPointerException();
			}
			ClientSessionImpl session = new ClientSessionImpl(sessionRefId);
			ClientSessionListener listener = app.login(session);
			session.registerListener(listener);
		}
	}
	
}
