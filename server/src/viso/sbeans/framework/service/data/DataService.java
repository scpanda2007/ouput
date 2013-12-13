package viso.sbeans.framework.service.data;

import java.util.concurrent.ConcurrentHashMap;

public class DataService {
	
	private ConcurrentHashMap<String,Object> services = new ConcurrentHashMap<String,Object>();
	
	private static DataService instance = new DataService();
	
	public static DataService getInstance(){
		return instance;
	}
	
	public <T> T getService(String name,Class<T> klass){
		Object obj = services.get(name);
		if(obj==null)return null;
		return klass.cast(obj);
	}
	
	public Object registerService(String name,Object service){
		return services.putIfAbsent(name, service);
	}
}
