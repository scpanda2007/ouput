package viso.util.tools;

import java.util.Properties;

public class PropertiesWrapper {
	
	private final Properties properties;
	
	public PropertiesWrapper(Properties properties){
		this.properties = properties;
	}
	
	public String getProperty(String arg0, String arg1){
		return properties.getProperty(arg0, arg1);
	}
	
	public String getProperty(String arg0){
		return properties.getProperty(arg0);
	}
	
	public long getIntProperty(String arg0){
		return Long.valueOf(properties.getProperty(arg0));
	}
	
	public int getIntProperty(String arg0, int arg1){
		return Integer.valueOf(properties.getProperty(arg0, ""+arg1));
	}
	
	public long getLongProperty(String arg0, long arg1){
		return Long.valueOf(properties.getProperty(arg0, ""+arg1));
	}
	
	public int getIntProperty(String arg0, int arg1, int min, int max){
		int value = getIntProperty(arg0, arg1);
		if(value >= min && value<=max) return value;
		return arg1;
	}

	public long getLongProperty(String arg0,
			long default_, long min, long max) {
		long value = getLongProperty(arg0, default_);
		if(value >= min && value<=max) return value;
		return default_;
	}

	public <T> T getClassInstanceProperty(String arg0,
			String default_, Class<T> class1, Class<?>[] classes,
			Object ...arg2) throws Exception {
		// TODO Auto-generated method stub
		Object obj = Class.forName(properties.getProperty(arg0, default_)).getConstructor(classes).newInstance(arg2);
		return class1.cast(obj);
	}

	public boolean getBooleanProperty(
			String arg0, boolean defaultBoolean) {
		// TODO Auto-generated method stub
		return Boolean.parseBoolean(properties.getProperty(arg0,(new Boolean(defaultBoolean)).toString()));
	}
}
