package viso.test.framework.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.util.tools.LoggerWrapper;

public class TestLoggerWrapper extends LoggerWrapper{

	private static ConsoleHandler handler = new ConsoleHandler();
	
	static{
		handler.setLevel(Level.ALL);
	}
	
	public TestLoggerWrapper(Logger logger) {
		super(logger);
		// TODO Auto-generated constructor stub
		logger.setLevel(Level.ALL);
		logger.addHandler(handler);
	}

}
