package viso.sbeans.framework.session;


import java.nio.ByteBuffer;

import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.net.ProtocolHandler;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolHandler;
import viso.sbeans.framework.session.ClientSession.SendEvent;
import viso.sbeans.framework.session.ClientSessionService.Action;

//
public class ClientSessionHandler implements SessionProtocolHandler{

	private final ProtocolHandler protocol;
	
	public ClientSessionHandler(ProtocolHandler protocol){
		this.protocol = protocol;
	}
	
	@Override
	public void handleSessionMessage(MessageBuffer message,
			RequestCompletionHandler<Void> handler) {
		// TODO Auto-generated method stub
		
	}
	
	class SendMessageAction implements Action{
		final SendEvent event;
		public SendMessageAction(SendEvent event){
			this.event = event;
		}
		@Override
		public void doAction() {
			// TODO Auto-generated method stub
			try{
				protocol.write(ByteBuffer.wrap(event.message));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
}
