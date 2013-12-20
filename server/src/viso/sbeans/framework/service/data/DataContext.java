package viso.sbeans.framework.service.data;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import viso.sbeans.framework.store.DataStore;
import viso.sbeans.framework.store.data.ClassSerializer;
import viso.sbeans.framework.store.data.ClassTables;
import viso.sbeans.framework.store.data.DataObject;
import viso.sbeans.framework.store.data.DataObjectReference;
import viso.sbeans.framework.store.data.FlushInfo;
import viso.sbeans.framework.transaction.TransactionListener;
import viso.sbeans.framework.transaction.VTransaction;

public class DataContext implements TransactionListener{
	
	public final DataStore store;
	ClassTables tables;
	public final ClassSerializer serializer;
	public final VTransaction transaction;
	
	SortedMap<String,DataObjectReference<?>> refs = new TreeMap<String,DataObjectReference<?>>();
	Map<DataObject,DataObjectReference<?>> objects = new IdentityHashMap<DataObject,DataObjectReference<?>>();
	
	public DataContext(VTransaction transaction,DataStore store,ClassTables tables){
		this.store = store;
		this.transaction = transaction;
		this.tables = tables;
		this.serializer = tables.createClassSerializer(transaction);
		this.transaction.registerListener(this);
	}
	
	public List<FlushInfo> flushAll(){
		List<FlushInfo> flushes = new ArrayList<FlushInfo>();
		Iterator<Map.Entry<String, DataObjectReference<?>>> iter = refs.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, DataObjectReference<?>> entry = iter.next();
			FlushInfo flush = entry.getValue().flush();
			if(flush==null)continue;
			flushes.add(flush);
		}
		refs.clear();
		objects.clear();
		return flushes;
	}
	
	public DataObjectReference<?> find(String key){
		return refs.get(key);
	}
	
	public void unregister(String key){
		refs.remove(key);
	}

	@Override
	public void beforeComplete(VTransaction transaction) {
		// TODO Auto-generated method stub
		
	}
	
}
