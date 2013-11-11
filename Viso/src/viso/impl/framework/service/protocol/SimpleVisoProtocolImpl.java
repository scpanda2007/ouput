package viso.impl.framework.service.protocol;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadPendingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.app.Delivery;
import viso.framework.nio.ClosedAsynchronousChannelException;
import viso.framework.service.protocol.ProtocolListener;
import viso.framework.service.protocol.RequestCompletionHandler;
import viso.framework.service.protocol.SessionProtocol;
import viso.framework.service.protocol.SessionProtocolHandler;
import viso.framework.service.protocol.simple.SimpleSgsProtocol;
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
	
	/** The completion handler for writing to the I/O channel. */
	private volatile WriteHandler writeHandler = new ConnectedWriteHandler();
	
	/** A lock for {@code loginHandled} and {@code messageQueue} fields. */
	private final Object lock = new Object();
	
	/** Indicates whether the client's login ack has been sent. */
	private boolean loginHandled = false;

	/** Messages enqueued to be sent after a login ack is sent. */
	private List<ByteBuffer> messageQueue = new ArrayList<ByteBuffer>();
	
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
	
	/* -- I/O completion handlers -- */

	/** A completion handler for writing to a connection. */
	private abstract class WriteHandler implements
			CompletionHandler<Void, Void> {
		/** Writes the specified message. */
		abstract void write(ByteBuffer message);
	}

	/** A completion handler for writing that always fails. */
	private class ClosedWriteHandler extends WriteHandler {

		ClosedWriteHandler() {
		}

		@Override
		void write(ByteBuffer message) {
			throw new ClosedAsynchronousChannelException();
		}

		public void completed(Void result, Void attr) {
			throw new AssertionError("should be unreachable");
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub

		}
	}

	/** A completion handler for writing to the session's channel. */
	private class ConnectedWriteHandler extends WriteHandler {

		/**
		 * The lock for accessing the fields {@code pendingWrites} and
		 * {@code isWriting}. The locks {@code lock} and {@code writeLock}
		 * should only be acquired in that specified order.
		 */
		private final Object writeLock = new Object();

		/** An unbounded queue of messages waiting to be written. */
		private final LinkedList<ByteBuffer> pendingWrites = new LinkedList<ByteBuffer>();

		/** Whether a write is underway. */
		private boolean isWriting = false;

		/** Creates an instance of this class. */
		ConnectedWriteHandler() {
		}

		/**
		 * Adds the message to the queue, and starts processing the queue if
		 * needed.
		 */
		@Override
		void write(ByteBuffer message) {
			if (message.remaining() > SimpleSgsProtocol.MAX_PAYLOAD_LENGTH) {
				throw new IllegalArgumentException("message too long: "
						+ message.remaining() + " > "
						+ SimpleSgsProtocol.MAX_PAYLOAD_LENGTH);
			}
			boolean first;
			synchronized (writeLock) {
				first = pendingWrites.isEmpty();
				pendingWrites.add(message);
			}
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST,
						"write protocol:{0} message:{1} first:{2}",
						SimpleVisoProtocolImpl.this,
						HexDumper.format(message, 0x50), first);
			}
			if (first) {
				processQueue();
			}
		}

		/** Start processing the first element of the queue, if present. */
		private void processQueue() {
			ByteBuffer message;
			synchronized (writeLock) {
				if (isWriting) {
					return;
				}
				message = pendingWrites.peek();
				if (message == null) {
					return;
				}
				isWriting = true;
			}
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST,
						"processQueue protocol:{0} size:{1,number,#} head={2}",
						SimpleVisoProtocolImpl.this, pendingWrites.size(),
						HexDumper.format(message, 0x50));
				message.mark();
			}
			try {
				asyncMsgChannel.write(message, this);
			} catch (RuntimeException e) {
				logger.logThrow(Level.SEVERE, e, "{0} processing message {1}",
						SimpleVisoProtocolImpl.this,
						HexDumper.format(message, 0x50));
				throw e;
			}
		}

		/** Done writing the first request in the queue. */
		public void completed(Void result, Void attr) {
			ByteBuffer message;
			synchronized (writeLock) {
				message = pendingWrites.remove();
				isWriting = false;
			}
			if (logger.isLoggable(Level.FINEST)) {
				ByteBuffer resetMessage = message.duplicate();
				resetMessage.reset();
				logger.log(Level.FINEST,
						"completed write protocol:{0} message:{1}",
						SimpleVisoProtocolImpl.this,
						HexDumper.format(resetMessage, 0x50));
			}

			processQueue();

			// try {
			// // result.getNow();
			// /* Keep writing */
			//
			// } catch (ExecutionException e) {
			// /*
			// * TBD: If we're expecting the session to close, don't
			// * complain.
			// */
			// if (logger.isLoggable(Level.FINE)) {
			// logger.logThrow(Level.FINE, e,
			// "write protocol:{0} message:{1} throws",
			// SimpleSgsProtocolImpl.this,
			// HexDumper.format(message, 0x50));
			// }
			synchronized (writeLock) {
				pendingWrites.clear();
			}
			close();
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			// TODO Auto-generated method stub

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
	
	/**
	 * Writes a message to the underlying connection if login has been handled,
	 * otherwise enqueues the message to be sent when the login has not yet been
	 * handled.
	 * 
	 * @param buf
	 *            a buffer containing a complete protocol message
	 */
	protected final void write(ByteBuffer buf) {
		synchronized (lock) {
			if (!loginHandled) {
				messageQueue.add(buf);
			} else {
				writeNow(buf, false);
			}
		}
	}
	
	/**
	 * Writes a message to the underlying connection.
	 * 
	 * @param message
	 *            a buffer containing a complete protocol message
	 * @param flush
	 *            if {@code true}, then set the {@code loginHandled} flag to
	 *            {@code true} and flush the message queue
	 */
	protected final void writeNow(ByteBuffer message, boolean flush) {
		try {
			writeHandler.write(message);
		} catch (RuntimeException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.logThrow(Level.WARNING, e,
						"writeNow protocol:{0} throws", this);
			}
		}

		if (flush) {
			synchronized (lock) {
				loginHandled = true;
				for (ByteBuffer nextMessage : messageQueue) {
					try {
						writeHandler.write(nextMessage);
					} catch (RuntimeException e) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.logThrow(Level.WARNING, e,
									"writeNow protocol:{0} throws", this);
						}
					}
				}
				messageQueue.clear();
			}
		}
	}
	
	/**
	 * Writes the specified buffer, satisfying the specified delivery
	 * requirement.
	 * 
	 * <p>
	 * This implementation writes the buffer reliably, because this protocol
	 * only supports reliable delivery.
	 * 
	 * <p>
	 * A subclass can override the {@code writeBuffer} method if it supports
	 * other delivery guarantees and can make use of alternate transports for
	 * those other delivery requirements.
	 * 
	 * @param buf
	 *            a byte buffer containing a protocol message
	 * @param delivery
	 *            a delivery requirement
	 */
	protected void writeBuffer(ByteBuffer buf, Delivery delivery) {
		write(buf);
	}

	@Override
	public void channelJoin(String name, BigInteger channelId, Delivery delivery)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void handleMessageReceived(byte opcode, MessageBuffer msg) {
		// TODO Auto-generated method stub
		logger.log(Level.CONFIG, "just test , get opcode : "+opcode);
		logger.log(Level.CONFIG, " get client message :: "+msg.getString());
		writeNow(ByteBuffer.wrap(" response from server".getBytes()),true);
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
		if(logger.isLoggable(Level.FINEST)){
			logger.log(Level.FINEST, "关闭channel, protocol {0}", this);
		}
		if(isOpen()){
			try{
				asyncMsgChannel.close();
			}catch(IOException e){
				
			}
		}
		readHandler = new ClosedReadHandler();
		writeHandler = new ClosedWriteHandler();
		if(protocolHandler != null){
			SessionProtocolHandler handler = protocolHandler;
			protocolHandler = null;
			handler.disconnect(new RequestHandler());
		}
		
	}
	
	/**
	 * A completion handler that is notified when its associated request has
	 * completed processing.
	 */
	private class RequestHandler implements RequestCompletionHandler<Void> {

		/**
		 * {@inheritDoc}
		 * 
		 * <p>
		 * This implementation schedules a task to resume reading.
		 */
		public void completed(Void future) {
			scheduleRead();
		}
	}


	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return asyncMsgChannel.isOpen();
	}

}
