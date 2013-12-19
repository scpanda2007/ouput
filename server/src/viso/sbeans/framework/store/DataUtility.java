package viso.sbeans.framework.store;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import viso.sbeans.framework.store.db.BDBDatabase;
import viso.sbeans.framework.store.db.DbCursor;
import viso.sbeans.framework.store.db.DbEnvironment;
import viso.sbeans.framework.store.db.DbTransaction;

public class DataUtility {

	private static final int SHASize = 20;
	private static final byte kDataClassInfoKey = 0;
	private static final byte kDataClassIdKey = 1;
	
	private static final ThreadLocal<MessageDigest> messageDigest =
		new ThreadLocal<MessageDigest>(){
			@Override
			protected MessageDigest initialValue(){
				try {
					return MessageDigest.getInstance("SHA-1");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					throw new AssertionError(e);
				}
			}
		};
	
	private static byte[] getDataClassInfoKey(byte[] classinfo){
		MessageDigest digest = messageDigest.get();
		byte key[] = new byte[1+SHASize];
		key[0] = kDataClassInfoKey;
		try {
			digest.update(classinfo);
			int num = digest.digest(key, 1, SHASize);
			assert num == SHASize;
			return key;
		} catch (DigestException e) {
			// TODO Auto-generated catch block
			throw new AssertionError(e);
		}
	}
	
	private static byte[] getDataClassIdKey(int classId){
		byte[] key = DataEncoder.encodeInt(classId, 1);
		key[0] = kDataClassIdKey;
		return key;
	}
	
	/**
	 * 从类数据库中获取id对应的解析类ObjectStreamClass 的字节串
	 * */
	public static byte[] getClassInfo(BDBDatabase classDb, int classId, 
			DbEnvironment env, int timeout){
		DbTransaction dbTxn = env.beginTransaction(timeout);
		boolean done = false;
		try{
			byte[] result = classDb.get(getDataClassIdKey(classId), dbTxn, false);
			dbTxn.commit();
			done = true;
			return result;
		}finally{
			if(!done){
				dbTxn.abort();
			}
		}
	}
	
	/**
	 * 从类数据库中查找一个类的id,如果没有就生成一个并插入
	 * */
	public static int getClassId(BDBDatabase classDb, byte[] classInfo,
			DbEnvironment env, int timeout) {
		// TODO Auto-generated method stub
		byte[] classInfoKey = getDataClassInfoKey(classInfo);
		boolean done = false;
		DbTransaction dbTxn = env.beginTransaction(timeout, true);
		try {
			int result;
			byte[] classIdKey = classDb.get(classInfoKey, dbTxn, false);
			if (classIdKey != null) {
				result = DataEncoder.decodeInt(classIdKey, 1);
			} else {
				DbCursor cursor = classDb.openCursor(dbTxn);
				try {
					result = cursor.findLast() ? DataEncoder.decodeInt(
							cursor.getKey(), 1) : 0;
					result += 1;
					classIdKey = getDataClassIdKey(result);
					if (!cursor.putNoOverWrite(classIdKey, classInfo)) {
						throw new IllegalStateException(
								"classIdKey already exist");
					}
				}finally{
					cursor.close();
				}
				if(!classDb.putNoOverWrite(classInfoKey, classIdKey, dbTxn)){
					throw new IllegalStateException(
					"classInfoKey already exist");
				}
			}
			dbTxn.commit();
			done = true;
			return result;
		} finally {
			if (!done) {
				dbTxn.abort();
			}
		}
	}

}
