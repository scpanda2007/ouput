package viso.util.tools;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerWrapper{
	
	private final Logger logger;
	
	private static FileHandler fileHander;
	
	static{
		try {
			fileHander = new FileHandler("log/viso%g_%u.log", 1000000, 1);
			fileHander.setLevel(Level.ALL);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LoggerWrapper(Logger logger){
		this.logger = logger;
		logger.setLevel(Level.ALL);
		logger.addHandler(fileHander);
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
