package viso.impl.framework.service.protocol;

import java.io.IOException;
import java.nio.channels.AsynchronousByteChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.framework.service.net.ConnectionHandler;
import viso.framework.service.net.Transport;
import viso.framework.service.protocol.ProtocolAcceptor;
import viso.framework.service.protocol.ProtocolDescriptor;
import viso.framework.service.protocol.ProtocolListener;
import viso.impl.framework.service.net.TcpTransport;
import viso.util.tools.LoggerWrapper;
import viso.util.tools.PropertiesWrapper;

public class SimpleVisoProtocolAcceptor implements ProtocolAcceptor{

	/** The package name. */
	private static final String PKG_NAME = "viso.framework.service.protocol";
	
	private static LoggerWrapper logger = new LoggerWrapper(Logger.getLogger(PKG_NAME+".acceptor"));
	
	public static final String TRANSPORT_PROPERTY = PKG_NAME + ".transport";
	
	public static final String DEFAULT_TRANSPORT = "viso.impl.framework.service.net.TcpTransport";
	
	private Transport transport;
	
	public SimpleVisoProtocolAcceptor(Properties properties) throws Exception{
		PropertiesWrapper wrappedProps = new PropertiesWrapper(properties);
		try {
			transport = wrappedProps.getClassInstanceProperty(TRANSPORT_PROPERTY, DEFAULT_TRANSPORT, Transport.class, new Class[]{Properties.class}, properties);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.logThrow(Level.CONFIG, e, "failed to initialized SimpleVisoProtocolAccepter ");
			throw e;
		}
	}
	
	@Override
	public ProtocolDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(ProtocolListener listener) throws IOException {
		// TODO Auto-generated method stub
		transport.accept(new ConnectionHandlerImpl(listener));
	}
	
	/**
	 * Transport connection handler.
	 */
	private class ConnectionHandlerImpl implements ConnectionHandler {

		private final ProtocolListener protocolListener;

		ConnectionHandlerImpl(ProtocolListener protocolListener) {
			if (protocolListener == null) {
				throw new NullPointerException("null protocolListener");
			}
			this.protocolListener = protocolListener;
		}

		/** {@inheritDoc} */
		public void newConnection(AsynchronousByteChannel byteChannel)
				throws Exception {
			new SimpleVisoProtocolImpl(protocolListener,
					SimpleVisoProtocolAcceptor.this, byteChannel,
					1024);
		}

		/** {@inheritDoc} */
		public void shutdown() {
			logger.log(Level.SEVERE, "transport unexpectly shutdown");
			close();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		transport.shutdown();
	}

}
