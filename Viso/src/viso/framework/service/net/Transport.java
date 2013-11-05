package viso.framework.service.net;

import java.io.IOException;

public interface Transport {
	void accept(ConnectionHandler handler) throws IOException;
	void shutdown();
    TransportDescriptor getDescriptor();
}
