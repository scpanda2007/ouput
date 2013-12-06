package viso.sbeans.framework.protocol;

import viso.sbeans.framework.net.ProtocolHandler;

/**会话协议接收器*/
public interface SessionProtocolAcceptor {
	public void LoginNow(ProtocolHandler proHandler, RequestCompletion<SessionProtocolHandler> request);
}
