package viso.sbeans.framework.protocol;

import viso.sbeans.framework.net.ProtocolHandler;

/**�ỰЭ�������*/
public interface SessionProtocolAcceptor {
	public void LoginNow(ProtocolHandler proHandler, RequestCompletion<SessionProtocolHandler> request);
}
