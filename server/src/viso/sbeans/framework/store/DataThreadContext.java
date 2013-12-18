package viso.sbeans.framework.store;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import viso.sbeans.framework.transaction.VTransaction;

class DataContext{
	VTransaction transaction;
	
	private final SortedMap<Long,DataObjectRefrence<?>> oids = 
			new TreeMap<Long,DataObjectRefrence<?>>();
	
	private final Map<DataObject,DataObjectRefrence<?>> refs = 
			new IdentityHashMap<DataObject,DataObjectRefrence<?>>();
	
	public DataContext(){}

	public DataObjectRefrence<?> find(long oid){
		return oids.get(oid);
	}
	
	public DataObjectRefrence<?> find(DataObject obj){
		return refs.get(obj);
	}
	
	public void put(long oid,DataObject obj){
		
	}
}

public class DataThreadContext {
	
	private ThreadLocal<DataContext> currentThreadContext = new ThreadLocal<DataContext>();
	
	public DataThreadContext(){}
	
	public DataContext getContext(){
		return currentThreadContext.get();
	}
}
