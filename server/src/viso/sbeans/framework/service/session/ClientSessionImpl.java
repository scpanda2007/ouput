package viso.sbeans.framework.service.session;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import viso.sbeans.framework.service.data.DataService;


public class ClientSessionImpl implements ClientSession{
	
	ClientSessionListener listener;
	ClientSessionService sessionService;
	private static final String keyPrefix = "viso.sbeans.framework.session.ClientSession";
	
	private final BigInteger sessionId;
	
	public ClientSessionImpl(BigInteger sessionRefId,ClientSessionService sessionService){
		DataService.getInstance().registerService(keyPrefix+sessionRefId, this);
		this.sessionService = sessionService;
		sessionId = sessionRefId;
		
	}
	
	public void registerListener(ClientSessionListener listener){
		this.listener = listener;
	}
	
	public ClientSessionListener getListener(){
		return listener;
	}
	
	public static ClientSessionImpl getSession(BigInteger sessionRefId){
		return DataService.getInstance().getService(keyPrefix+sessionRefId,ClientSessionImpl.class);
	}
	
	@Override
	public void send(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		byte message[] = new byte[buffer.remaining()];
		buffer.asReadOnlyBuffer().get(message);
		sessionService.getClientSessionServer(sessionId).send(message, sessionId);
	}
	
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return true;
	}
}
