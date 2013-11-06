package viso.test.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.impl.framework.service.net.TcpTransport;
import viso.test.TestStandardProperties;
import viso.util.tools.LoggerWrapper;

public class TestProperties {
	
	private static TestLoggerWrapper logger = new TestLoggerWrapper(Logger.getLogger("viso.TestProperties"));
	
	static private Properties properties;
	
	private static final String proPath = "conf/test/testproperties";
	
	private static final String testDefaultHostProperty = "127.0.0.1";
	
	public static Properties getProperties() {
		if(properties==null){
			properties = new Properties();
			File file = new File(proPath);
			if(!file.exists()){
				OutputStream out;
				try {
					file.createNewFile();
					out = new BufferedOutputStream(new FileOutputStream(proPath));
					properties.put(TcpTransport.LISTEN_HOST_PROPERTY, testDefaultHostProperty);
					properties.store(out, "create testproperties...");
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalStateException("failed to create properties file");
				}
				logger.log(Level.CONFIG, " succeed build testproperties file");
			}else{
				InputStream in;
				try {
					in = new BufferedInputStream(new FileInputStream(proPath));
					properties.load(in);
					in.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalStateException("failed to load properties file");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalStateException("failed to load properties file");
				}
				logger.log(Level.CONFIG, " succeed load testproperties file");
			}
		}
		return properties;
	}
	
}
