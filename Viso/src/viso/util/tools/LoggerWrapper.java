package viso.util.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerWrapper{
	
	private final Logger logger;
	
	public LoggerWrapper(Logger logger){
		this.logger = logger;
	}
	
	public void log(Level arg0, String arg1){
		logger.log(arg0, arg1);
	}
	
	public void log(Level arg0, String arg1, Object ...arg2){
		String str = String.format(arg1, arg2);
		logger.log(arg0, str);
	}
	
	public void log(Level arg0, String arg1, Throwable arg2){
		logger.log(arg0, arg1, arg2);
	}
	
	public boolean isLoggable(Level arg0){
		return logger.isLoggable(arg0);
	}
	
	public void logThrow(Level arg0, Throwable arg1, String arg2, Object ...arg3){
		String str = String.format(arg2, arg3);
		logger.log(arg0, str, arg1);
	}
}
