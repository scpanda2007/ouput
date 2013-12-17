package viso.sbeans.framework.store;

import viso.sbeans.framework.store.db.BDBDatabase;
import viso.sbeans.framework.store.db.DbTransaction;

public class DataStoreHelper {
	private static final short kObjIdInfo = 0;
	public static long getObjectIdInfo(BDBDatabase info, DbTransaction transaction, int increment){
		byte[] key = DataEncoder.encodeShort(kObjIdInfo);
		byte[] lastObjIds = info.get(key, transaction, true);
		long lastObjId = 0L;
		if(lastObjIds!=null)DataEncoder.decodeLong(lastObjIds);
		info.put(key, DataEncoder.encodeLong(lastObjId+increment), transaction);
		return lastObjId;
	}
}
