package viso.sbeans.framework.service.session;

import java.nio.ByteBuffer;

public interface ClientSession {
	public void send(ByteBuffer message);
	public boolean isConnected();
}
