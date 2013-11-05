package viso.framework.service.net;

import java.io.IOException;

import viso.app.Delivery;

public interface Transport {
	void accept(ConnectionHandler handler) throws IOException;
	void shutdown();
    TransportDescriptor getDescriptor();
	Delivery getDelivery();
}
