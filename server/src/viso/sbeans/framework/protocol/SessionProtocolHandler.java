package viso.sbeans.framework.protocol;

import viso.sbeans.framework.net.MessageBuffer;

/**�ỰЭ�鴦����*/
public interface SessionProtocolHandler {
	public void handleSessionMessage(MessageBuffer message,RequestCompletionHandler<Void> handler);
}
