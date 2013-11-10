package viso.test.framework.service.net;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.framework.service.protocol.RequestCompletionHandler;
import viso.framework.service.protocol.SessionProtocolHandler;
import viso.util.tools.LoggerWrapper;

public class TestSessionProtocolHander implements SessionProtocolHandler{

	private static LoggerWrapper logger = new LoggerWrapper(Logger.getLogger(TestSessionProtocolHander.class.getName()));
	
	public TestSessionProtocolHander(){}
	
	@Override
	public void channelMessage(BigInteger channelId, ByteBuffer message,
			RequestCompletionHandler<Void> completionHandler) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, " channelMessage done");
	}

	@Override
	public void disconnect(RequestCompletionHandler<Void> completionHandler) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, " disconnect done");
		completionHandler.completed(null);
	}

	@Override
	public void logoutRequest(RequestCompletionHandler<Void> completionHandler) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, " logoutRequest done");
	}

	@Override
	public void sessionMessage(ByteBuffer message,
			RequestCompletionHandler<Void> completionHandler) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, " sessionMessage done");
	}

}
