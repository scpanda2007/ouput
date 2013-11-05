/**
 * 
 */
package viso.impl.framework.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.framework.service.net.ConnectionHandler;
import viso.framework.service.net.Transport;
import viso.framework.service.net.TransportDescriptor;
import viso.util.tools.LoggerWrapper;
import viso.util.tools.NamedThreadFactory;
import viso.util.tools.PropertiesWrapper;

/**
 * @author delljy
 *
 */
public class TcpTransport implements Transport {

	private static final String PKG_NAME = "viso.impl.framework.service.net";

	private static final LoggerWrapper logger = new LoggerWrapper(Logger
			.getLogger(PKG_NAME));

	///////////////////////////////////////////////////////////////////////////
	/** The default port: {@value #DEFAULT_PORT}. */
	public static final int DEFAULT_PORT = 62964;

	/**
	 * The server listen address property.
	 * This is the host interface we are listening on. Default is listen
	 * on all interfaces.
	 */
	public static final String LISTEN_HOST_PROPERTY = PKG_NAME
			+ ".listen.address";

	/** The name of the server port property. */
	public static final String LISTEN_PORT_PROPERTY = PKG_NAME + ".listen.port";

	/** The name of the acceptor backlog property. */
	public static final String ACCEPTOR_BACKLOG_PROPERTY = PKG_NAME
			+ ".acceptor.backlog";

	/** The default acceptor backlog (&lt;= 0 means default). */
	private static final int DEFAULT_ACCEPTOR_BACKLOG = 0;

	/** The acceptor backlog. */
	private final int acceptorBacklog;

	///////////////////////////////////////////////////////////////////////////
	/** The listen address. */
	final InetSocketAddress listenAddress;

	/** The async channel group for this service. */
	private final AsynchronousChannelGroup asyncChannelGroup;

	/** The acceptor for listening for new connections. */
	volatile AsynchronousServerSocketChannel acceptor;

	/** The transport descriptor */
	private final TcpDescriptor descriptor;

	/** The acceptor listener. */
	private AcceptorListener acceptorListener = null;

	public TcpTransport(Properties properties) {
		logger.log(Level.CONFIG, "Creating TcpTransport");
		if (properties == null) {
			throw new NullPointerException("properties is null");
		}
		PropertiesWrapper wrappedProps = new PropertiesWrapper(properties);

		acceptorBacklog = wrappedProps.getIntProperty(
				ACCEPTOR_BACKLOG_PROPERTY, DEFAULT_ACCEPTOR_BACKLOG);

		String host = properties.getProperty(LISTEN_HOST_PROPERTY);
		int port = wrappedProps.getIntProperty(LISTEN_PORT_PROPERTY,
				DEFAULT_PORT, 1, 65535);

		try {
			// If no host address is supplied, default to listen on all
			// interfaces on the local host.
			//
			listenAddress = host == null ? new InetSocketAddress(port)
					: new InetSocketAddress(host, port);
			descriptor = new TcpDescriptor(host == null ? InetAddress
					.getLocalHost().getHostName() : host, listenAddress
					.getPort());

			AsynchronousChannelProvider provider = AsynchronousChannelProvider
					.provider();
			asyncChannelGroup = provider.openAsynchronousChannelGroup(Executors
					.newCachedThreadPool(new NamedThreadFactory(
							"TcpTransport-Acceptor")), 1);
			acceptor = provider
					.openAsynchronousServerSocketChannel(asyncChannelGroup);
			try {
				acceptor.bind(listenAddress, acceptorBacklog);
				if (logger.isLoggable(Level.CONFIG)) {
					logger.log(Level.CONFIG,
							"acceptor bound to host: {0} port:{1,number,#}",
							descriptor.hostName, descriptor.listeningPort);
				}
			} catch (Exception e) {
				logger.logThrow(Level.WARNING, e,
						"acceptor failed to listen on {0}", listenAddress);
				try {
					acceptor.close();
				} catch (IOException ioe) {
					logger.logThrow(Level.WARNING, ioe,
							"problem closing acceptor");
				}
				throw e;
			}

			logger.log(Level.CONFIG, "Created TcpTransport with properties:"
					+ "\n  " + ACCEPTOR_BACKLOG_PROPERTY + "="
					+ acceptorBacklog + "\n  " + LISTEN_HOST_PROPERTY + "="
					+ host + "\n  " + LISTEN_PORT_PROPERTY + "=" + port);

		} catch (Exception e) {
			if (logger.isLoggable(Level.CONFIG)) {
				logger.logThrow(Level.CONFIG, e,
						"Failed to create TCP transport");
			}
			shutdown();
			throw new RuntimeException(e);
		}
	}

	//    volatile IoFuture<?> acceptFuture = null;

	@Override
	public TransportDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return this.descriptor;
	}

	/** {@inheritDoc} */
	public synchronized void accept(ConnectionHandler handler) {
		if (handler == null) {
			throw new NullPointerException("null handler");
		} else if (!acceptor.isOpen()) {
			throw new IllegalStateException("transport has been shutdown");
		}

		if (acceptorListener != null) {
			throw new IllegalStateException("accept already called");
		}
		acceptorListener = new AcceptorListener(handler);

		acceptor.accept(null, acceptorListener);
		logger.log(Level.CONFIG, "transport accepting connections");
	}

	/** {@inheritDoc} */
	public synchronized void shutdown() {

		if (acceptor != null && acceptor.isOpen()) {
			try {
				acceptor.close();
			} catch (IOException e) {
				logger.logThrow(Level.FINEST, e, "closing acceptor throws");
				// swallow exception
			}
		}

		if (asyncChannelGroup != null && !asyncChannelGroup.isShutdown()) {
			asyncChannelGroup.shutdown();
			boolean groupShutdownCompleted = false;
			try {
				groupShutdownCompleted = asyncChannelGroup.awaitTermination(1,
						TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.logThrow(Level.FINEST, e,
						"shutdown async group interrupted");
				Thread.currentThread().interrupt();
			}
			if (!groupShutdownCompleted) {
				logger.log(Level.WARNING, "forcing async group shutdown");
				try {
					asyncChannelGroup.shutdownNow();
				} catch (IOException e) {
					logger.logThrow(Level.FINEST, e,
							"shutdown async group throws");
					// swallow exception
				}
			}
			logger.log(Level.FINEST, "transport shutdown");
		}
	}

	/**
	 * Closes the current acceptor and opens a new one, binding it to the
	 * listen address specified during construction.  This method is
	 * invoked if a problem occurs handling a new connection or initiating
	 * another accept request on the current acceptor.
	 *
	 * @throws	IOException if the async channel group is shutdown, or
	 * 		a problem occurs creating the new acceptor or binding it to
	 * 		the listen address
	 */
	private synchronized void restart() throws IOException {
		if (asyncChannelGroup.isShutdown()) {
			throw new IOException("channel group is shutdown");
		}

		try {
			acceptor.close();
		} catch (IOException ex) {
			logger.logThrow(Level.FINEST, ex,
					"exception closing acceptor during restart");
		}
		acceptor = AsynchronousChannelProvider.provider()
				.openAsynchronousServerSocketChannel(asyncChannelGroup);

		acceptor.bind(listenAddress, acceptorBacklog);
	}

	/** A completion handler for accepting connections. */
	private class AcceptorListener implements
			CompletionHandler<AsynchronousSocketChannel, Void> {
		/** The connection handler. */
		private final ConnectionHandler connectionHandler;

		/**
		 * Constructs an instance with the specified {@code connectionHandler}.
		 *
		 * @param connectionHandler a connection handler
		 */
		AcceptorListener(ConnectionHandler connectionHandler) {
			this.connectionHandler = connectionHandler;
		}

		/** Handle new connection or report failure. */
		public void completed(AsynchronousSocketChannel result, Void attr) {
			try {
				try {
					AsynchronousSocketChannel newChannel = result;
					logger.log(Level.FINER, "Accepted {0}", newChannel);

					connectionHandler.newConnection(newChannel);

					// Resume accepting connections
					acceptor.accept(null, this);

				} catch (ExecutionException e) {
					throw (e.getCause() == null) ? e : e.getCause();
				}
			} catch (CancellationException e) {
				logger.logThrow(Level.FINE, e, "acceptor cancelled");
				//ignore
			} catch (Throwable e) {
				logger.logThrow(Level.SEVERE, e, "acceptor error on {0}",
						listenAddress);
				try {
					restart();

					// Resume accepting connections on new acceptor
					acceptor.accept(null, this);
				} catch (IOException ioe) {
					logger.logThrow(Level.FINEST, ioe,
							"exception during restart");
					shutdown();
					connectionHandler.shutdown();
				}
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub

		}
	}

}
