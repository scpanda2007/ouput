package viso.framework.service.net;

import java.nio.channels.AsynchronousByteChannel;

public interface ConnectionHandler {
	void newConnection(AsynchronousByteChannel channel) throws Exception;
	void shutdown();
}
