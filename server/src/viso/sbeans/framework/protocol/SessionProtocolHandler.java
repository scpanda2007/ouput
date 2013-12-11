package viso.sbeans.framework.protocol;

import viso.sbeans.framework.net.MessageBuffer;

/**会话协议处理器*/
public interface SessionProtocolHandler {
	public void handleSessionMessage(MessageBuffer message,RequestCompletionHandler<Void> handler);
}
