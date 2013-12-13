package viso.sbeans.framework.service.session;

import java.math.BigInteger;

import viso.sbeans.framework.service.data.DataService;


public class ClientSessionImpl implements ClientSession{
	ClientSessionListener listener;
	private static final String keyPrefix = "viso.sbeans.framework.session.ClientSession";
	public ClientSessionImpl(BigInteger sessionRefId){
		DataService.getInstance().registerService(keyPrefix+sessionRefId, this);
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
}
