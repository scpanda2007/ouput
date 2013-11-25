package viso.impl.framework.service.store.net;

import java.io.IOException;
import java.net.Socket;

/**
 * The client side of an experimental network protocol, not currently used, for
 * implementing DataStoreServer using sockets instead of RMI.
 */
/*
 * XXX: Limit connections and/or close unused connections?
 * XXX: Close and not return sockets on IOException?
 */
class DataStoreClientRemote extends DataStoreProtocolClient {

	/** The server host name. */
	private final String host;

	/** The server network port. */
	private final int port;

	/** Creates an instance for the specified host and port */
	DataStoreClientRemote(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * {@inheritDoc} <p>
	 *
	 * This implementation creates a socket.
	 */
	@Override
	DataStoreProtocol createHandler() throws IOException {
		Socket socket = new Socket(host, port);
		setSocketOptions(socket);
		return new DataStoreProtocol(socket.getInputStream(), socket
				.getOutputStream());
	}

	/** Sets TcpNoDelay and KeepAlive options, if possible. */
	private void setSocketOptions(Socket socket) {
		try {
			socket.setTcpNoDelay(true);
		} catch (Exception e) {
		}
		try {
			socket.setKeepAlive(true);
		} catch (Exception e) {
		}
	}
}
