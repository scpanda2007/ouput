package viso.sbeans.framework.store;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import viso.com.util.PropertiesWrapper;
import viso.sbeans.framework.kernel.ThreadContext;
import viso.sbeans.framework.store.db.BDBDatabase;
import viso.sbeans.framework.store.db.DbEnvironment;
import viso.sbeans.framework.store.db.DbTransaction;
import viso.sbeans.framework.transaction.VTransaction;

import static viso.com.util.Objects.checkNull;

public class DataStore {
	BDBDatabase infoDb;
	BDBDatabase klassDb;
	BDBDatabase objDb;
	BDBDatabase nameDb;
	public static final String STORE_DIR = "viso.sbeans.framework.store.dir";
	public static final String STORE_BASE = "viso.sbeans.framework.store.base";
	DbEnvironment env;
	
	private static final int kObjidIncrement = 1000;
	
	public DataStore(Properties property,String fileName){
		PropertiesWrapper prop = new PropertiesWrapper(property);
		String base = prop.getProperty(STORE_BASE);
		String dir = prop.getProperty(STORE_DIR);
		checkNull("store dir",dir);
		checkNull("store fileName",fileName);
		env = DbEnvironment.environment(base,dir);
		DbTransaction dbTxn = env.beginTransaction(200);
		infoDb = env.open(dbTxn, fileName+"_info", true);
		klassDb = env.open(dbTxn, fileName+"_class", true);
		objDb = env.open(dbTxn, fileName+"_obj", true);
		nameDb = env.open(dbTxn, fileName+"_name", true);
		dbTxn.commit();
	}
	
	public ConcurrentSkipListMap<DataObjectRefrence<?>,byte[]> managedObjects;
	
	public ObjectIdInfo idInfo = new ObjectIdInfo();
	
	private class ObjectIdInfo{
		private long nextObjid = 0;
		private long limitObjid = 0;
		public synchronized long getNextObjectId(DbTransaction dbTxn) {
			if (nextObjid >= limitObjid) {
				nextObjid = DataStoreHelper.getObjectIdInfo(infoDb, dbTxn,
						kObjidIncrement);
				limitObjid = nextObjid + kObjidIncrement;
			}
			return nextObjid;
		}
	}
	
	public <T extends DataObject> DataObjectRefrence<T> createObjectRef(T object){
		DbTransaction txn = checkVTxn();
		DataObjectRefrence<T> ref = new DataObjectRefrence<T>(object);
		managedObjects.putIfAbsent(ref, DataEncoder.encodeLong(idInfo.getNextObjectId(txn)));
		return ref;
	}
	
	private DbTransaction checkVTxn(){
		VTransaction transaction = ThreadContext.getTransaction();
		if(ThreadContext.getTransaction()==null){
			throw new IllegalStateException("Must be in an active transaction.");
		}
		DbTransaction dbTxn = txninfos.get(transaction);
		if(dbTxn==null){
			dbTxn = env.beginTransaction(100);
			DbTransaction tmp = txninfos.putIfAbsent(transaction, dbTxn);
			if(tmp!=null){
				dbTxn.abort();
				dbTxn = tmp;
			}
		}
		return dbTxn;
	}
	
	private ConcurrentHashMap<VTransaction,DbTransaction> txninfos = new ConcurrentHashMap<VTransaction,DbTransaction>();
	
	public void shutdown(){
		infoDb.close();
		klassDb.close();
		objDb.close();
		nameDb.close();
	}
}
