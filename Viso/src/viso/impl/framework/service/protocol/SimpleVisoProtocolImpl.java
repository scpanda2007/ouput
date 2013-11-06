package viso.impl.framework.service.protocol;

import java.nio.channels.AsynchronousByteChannel;
import java.util.logging.Logger;

import viso.app.Delivery;
import viso.framework.service.protocol.ProtocolListener;
import viso.framework.service.protocol.SessionProtocolHandler;
import viso.util.tools.LoggerWrapper;

public class SimpleVisoProtocolImpl {
	
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
		
	}
}
