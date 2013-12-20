package viso.sbeans.framework.service.data;

import java.util.concurrent.ConcurrentHashMap;

import viso.sbeans.framework.kernel.ThreadContext;
import viso.sbeans.framework.store.DataStore;
import viso.sbeans.framework.store.data.ClassTables;
import viso.sbeans.framework.store.data.DataObject;
import viso.sbeans.framework.store.data.DataObjectReference;
import viso.sbeans.framework.transaction.TransactionNotActiveException;
import viso.sbeans.framework.transaction.VTransaction;

public class DataService {
	
	private ConcurrentHashMap<String,Object> services = new ConcurrentHashMap<String,Object>();
	
	private ThreadLocal<DataContext> context = new ThreadLocal<DataContext>();
	
	private static DataService instance;
	
	private DataStore store;
	
	private ClassTables classes;
	
	private DataService(DataStore store){
		this.store = store;
		classes = new ClassTables(store);
	}
	
	public void shutdown(){
		this.store.shutdown();
	}
	
	public static DataService createDataService(DataStore store){
		if(instance!=null){
			throw new IllegalStateException("Already has a dataService.");
		}
		instance = new DataService(store);
		return instance;
	}
	
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
	
	public DataContext getContextNoJoin(){
		return context.get();
	}
	
	public DataContext joinDataContext(){
		if(context.get()!=null) return context.get();
		VTransaction transaction = ThreadContext.getTransaction();
		if(transaction==null || !transaction.active()){
			throw new TransactionNotActiveException("Need an active transaction to create context");
		}
		DataContext ctxt = new DataContext(transaction, store, classes);
		context.set(ctxt);
		return ctxt;
	}
	
	public DataObject getObject(String key,boolean lockWrite){
		DataObjectReference<?> ref = getReference(key,lockWrite);
		return ref==null? null : ref.get(lockWrite);
	}
	
	public void removeObject(DataObject object){
		this.joinDataContext().removeObject(object);
	}
	
	public DataObjectReference<?> addManaged(String key, DataObject object){
		return this.joinDataContext().addManaged(object, key);
	}
	
	public DataObjectReference<?> getReference(String key,boolean lockWrite){
		return this.joinDataContext().getReference(key,lockWrite);
	}
	
}
