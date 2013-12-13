package viso.sbeans.framework.service.session;

import viso.sbeans.framework.net.MessageBuffer;

public interface ClientSessionListener {
	public void handleSessionMessage(MessageBuffer message);
}
