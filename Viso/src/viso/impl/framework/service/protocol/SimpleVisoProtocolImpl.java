package viso.impl.framework.service.protocol;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadPendingException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.app.Delivery;
import viso.framework.nio.ClosedAsynchronousChannelException;
import viso.framework.service.protocol.ProtocolListener;
import viso.framework.service.protocol.SessionProtocol;
import viso.framework.service.protocol.SessionProtocolHandler;
import viso.util.tools.HexDumper;
import viso.util.tools.LoggerWrapper;
import viso.util.tools.MessageBuffer;

public class SimpleVisoProtocolImpl implements SessionProtocol {
	
	/** The number of bytes used to represent the message length. */
	private static final int PREFIX_LENGTH = 2;
	
	/** The logger for this class. */
	private static final LoggerWrapper staticLogger = new LoggerWrapper(
			Logger.getLogger(SimpleVisoProtocolImpl.class.getName()));
	
	/** 消息读写管道的一次封装 */
	private final AsynchronousMessageChannel asyncMsgChannel;

	/** 协议处理器 */
	protected volatile SessionProtocolHandler protocolHandler;

	/** This protocol's acceptor. */
	protected final SimpleVisoProtocolAcceptor acceptor;

	/** The logger for this instance. */
	protected final LoggerWrapper logger;

	/** The protocol listener. */
	protected final ProtocolListener listener;
	
	/** The completion handler for reading from the I/O channel. */
	private volatile ReadHandler readHandler = new ConnectedReadHandler();
	
	SimpleVisoProtocolImpl(ProtocolListener listener,
			SimpleVisoProtocolAcceptor acceptor,
			AsynchronousByteChannel byteChannel, int readBufferSize) {
		this(listener, acceptor, byteChannel, readBufferSize, staticLogger);
		/*
		 * TBD: It might be a good idea to implement high- and low-water marks
		 * for the buffers, so they don't go into hysteresis when they get full.
		 * -JM
		 */
		scheduleRead();
	}
	
	protected SimpleVisoProtocolImpl(ProtocolListener listener,
			SimpleVisoProtocolAcceptor acceptor,
			AsynchronousByteChannel byteChannel, int readBufferSize,
			LoggerWrapper logger) {
		// The read buffer size lower bound is enforced by the protocol acceptor
		assert readBufferSize >= PREFIX_LENGTH;
		this.asyncMsgChannel = new AsynchronousMessageChannel(byteChannel,
				readBufferSize);
		this.listener = listener;
		this.acceptor = acceptor;
		this.logger = logger;
	}
	
	protected final void scheduleRead(){
		readNow();
	}
	
	/**
	 * Resumes reading from the underlying connection.
	 */
	protected final void readNow() {
		if (isOpen()) {
			readHandler.read();
		} else {
			close();
		}
	}
	
	
	/** A completion handler for reading from a connection. */
	private abstract class ReadHandler implements
			CompletionHandler<ByteBuffer, Void> {
		/**
		 * Initiates the read request.
		 * 
		 * @throws AsynchronousCloseException
		 * @throws ExecutionException
		 * @throws InterruptedException
		 */
		abstract void read();
	}
	
	/** A completion handler for reading that always fails. */
	private class ClosedReadHandler extends ReadHandler {

		ClosedReadHandler() {
		}

		@Override
		void read() {
			throw new ClosedAsynchronousChannelException();
		}

		public void completed(ByteBuffer result, Void attr) {
			throw new AssertionError("should be unreachable");
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub

		}
	}
	
	/** A completion handler for reading from the session's channel. */
	private class ConnectedReadHandler extends ReadHandler {

		/**
		 * The lock for accessing the {@code isReading} field. The locks
		 * {@code lock} and {@code readLock} should only be acquired in that
		 * specified order.
		 */
		private final Object readLock = new Object();

		/** Whether a read is underway. */
		private boolean isReading = false;

		/** Creates an instance of this class. */
		ConnectedReadHandler() {
		}

		/**
		 * Reads a message from the connection.
		 * 
		 * @throws ExecutionException
		 * @throws InterruptedException
		 */
		@Override
		void read() {
			synchronized (readLock) {
				if (isReading) {
					throw new ReadPendingException();
				}
				isReading = true;
			}
			asyncMsgChannel.read(this);
		}

		/** Handles the completed read operation. */
		public void completed(ByteBuffer result, Void attr) {
			synchronized (readLock) {
				isReading = false;
			}
			try {
				ByteBuffer message = result;
				if (message == null) {
					close();
					return;
				}
				if (logger.isLoggable(Level.FINEST)) {
					logger.log(Level.FINEST,
							"completed read protocol:{0} message:{1}",
							SimpleVisoProtocolImpl.this,
							HexDumper.format(message, 0x50));
				}

				byte[] payload = new byte[message.remaining()];
				message.get(payload);

				// Dispatch
				MessageBuffer msg = new MessageBuffer(payload);
				byte opcode = msg.getByte();

				if (logger.isLoggable(Level.FINEST)) {
					logger.log(Level.FINEST, "processing opcode 0x{0}",
							Integer.toHexString(opcode));
				}

				handleMessageReceived(opcode, msg);

			} catch (Exception e) {

				/*
				 * TBD: If we're expecting the channel to close, don't complain.
				 */

				if (logger.isLoggable(Level.FINE)) {
					logger.logThrow(Level.FINE, e,
							"Read completion exception {0}", asyncMsgChannel);
				}
				close();
			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void channelJoin(String name, BigInteger channelId, Delivery delivery)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void handleMessageReceived(byte opcode, MessageBuffer msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void channelLeave(BigInteger channelId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void channelMessage(BigInteger channelId, ByteBuffer message,
			Delivery delivery) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect(DisconnectReason reason) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Delivery> getDeliveries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxMessageLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sessionMessage(ByteBuffer message, Delivery delivery)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

}
