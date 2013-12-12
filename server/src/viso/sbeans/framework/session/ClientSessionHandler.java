package viso.sbeans.framework.session;

import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.protocol.RequestCompletionHandler;
import viso.sbeans.framework.protocol.SessionProtocolHandler;

public class ClientSessionHandler implements SessionProtocolHandler{

	
	
	public ClientSessionHandler(){
		
	}
	
	@Override
	public void handleSessionMessage(MessageBuffer message,
			RequestCompletionHandler<Void> handler) {
		// TODO Auto-generated method stub
		
	}

}
