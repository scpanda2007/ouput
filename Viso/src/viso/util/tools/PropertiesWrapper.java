package viso.util.tools;

import java.lang.reflect.InvocationTargetException;
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
		throw new IllegalArgumentException(" the param "+arg0+" with value:"+value+" is not between "+min+" and "+max);
	}

	public long getLongProperty(String arg0,
			long default_, long min, long max) {
		long value = getLongProperty(arg0, default_);
		if(value >= min && value<=max) return value;
		throw new IllegalArgumentException(" the param "+arg0+" with value:"+value+" is not between "+min+" and "+max);
	}

	public <T> T getClassInstanceProperty(String arg0,
			String default_, Class<T> class1, Class<?>[] classes,
			Object ...arg2) {
		// TODO Auto-generated method stub
		Object obj;
		try {
			obj = Class.forName(properties.getProperty(arg0, default_)).getConstructor(classes).newInstance(arg2);
			return class1.cast(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof InvocationTargetException){
				if(((InvocationTargetException)e).getTargetException() instanceof RuntimeException ){
					throw ((RuntimeException)((InvocationTargetException)e).getTargetException());
				}
			}
			throw new IllegalStateException(e.getCause());
		}
	}

	public boolean getBooleanProperty(
			String arg0, boolean defaultBoolean) {
		// TODO Auto-generated method stub
		return Boolean.parseBoolean(properties.getProperty(arg0,(new Boolean(defaultBoolean)).toString()));
	}

	public <T extends Enum<T>> T getEnumProperty(String property,
			Class<T> class1, T defaultValue) {
		// TODO Auto-generated method stub
		return Enum.valueOf(class1, properties.getProperty(property,defaultValue.toString()));
	}
}
