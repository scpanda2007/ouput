package viso.sbeans.framework.service.session.test;

import viso.sbeans.app.AppListener;
import viso.sbeans.framework.net.MessageBuffer;
import viso.sbeans.framework.service.data.DataService;
import viso.sbeans.framework.service.session.ClientSession;
import viso.sbeans.framework.service.session.ClientSessionListener;

public class DummyApp implements AppListener{
	
	public DummyApp(){
		DataService.getInstance().registerService("viso.sbeans.app.AppListener", this);
	}
	
	@Override
	public ClientSessionListener login(ClientSession session) {
		// TODO Auto-generated method stub
		System.out.println("rece a ClientSession ");
		return new ClientSessionListenerImpl();
	}

	private class ClientSessionListenerImpl implements ClientSessionListener{

		@Override
		public void handleSessionMessage(MessageBuffer message) {
			// TODO Auto-generated method stub
			System.out.println("rece : "+message.readUTF()+" -- "+System.currentTimeMillis());
		}
		
	}
}
