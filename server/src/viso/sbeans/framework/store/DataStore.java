package viso.sbeans.framework.store;

import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import viso.sbeans.framework.store.db.BDBDatabase;
import viso.sbeans.framework.store.db.DbEnvironment;
import viso.sbeans.framework.store.db.DbTransaction;

import static viso.com.util.Objects.checkNull;

public class DataStore {

	Map<String, BDBDatabase> databases = new HashMap<String, BDBDatabase>();
	
	BDBDatabase classDb;
	
	Map<Integer, ObjectStreamClass> classDescs = new HashMap<Integer, ObjectStreamClass>();
	
	DbEnvironment env;

	Store store;
	
	public DataStore(Properties property) {

	}

	public void init(String base, String root,
			Set<String> fileNames){
		store = new Store(base,root,fileNames);
	}
	
	public void shutdown(){
		store.close();
	}
	
	private class Store {

		/**
		 * 初始化数据库
		 * */
		public Store(String base, String root,
				Set<String> fileNames) {
			if (env != null) {
				throw new IllegalStateException("DataStore already setup.");
			}
			if(fileNames.contains("classes")){
				throw new IllegalStateException("Illegal Param: classes.");
			}
			env = DbEnvironment.environment(base, root);
			DbTransaction txn = env.beginTransaction(100);
			try {
				for (String fileName : fileNames) {
					databases.put(fileName, env.open(txn, fileName, true));
				}
				classDb = env.open(txn, "classes", true);
				txn.commit();
			} catch (Exception e) {
				txn.abort();
				env.close();
				throw new IllegalStateException("Failed open databases.", e);
			}
		}

		/**
		 * 获得某个表的某个数据
		 * */
		public byte[] get(DbTransaction txn, String table, byte[] key,
				boolean update) {
			checkNull("Table name:", table);
			if (!databases.containsKey(table)) {
				throw new IllegalArgumentException("The table:" + table
						+ " do not exist.");
			}
			return databases.get(table).get(key, txn, update);
		}

		/**
		 * 以不覆盖的方式尝试写入一个值，并返回该键值的值是否本不存在，如果存在是否写入成功取决于数据看的配置是否允许同一键值有 多个值
		 * */
		public boolean putNoOverWrite(DbTransaction txn, String table,
				byte[] key, byte[] data) {
			checkNull("Table name:", table);
			if (!databases.containsKey(table)) {
				throw new IllegalArgumentException("The table:" + table
						+ " do not exist.");
			}
			return databases.get(table).putNoOverWrite(key, data, txn);
		}

		/**
		 * 写入一个值
		 * */
		public void put(DbTransaction txn, String table, byte[] key, byte[] data) {
			checkNull("Table name:", table);
			if (!databases.containsKey(table)) {
				throw new IllegalArgumentException("The table:" + table
						+ " do not exist.");
			}
			databases.get(table).put(key, data, txn);
		}

		/**
		 * 删除一个值 并返回是否存在这样一个值
		 * */
		public boolean delete(DbTransaction txn, String table, byte[] key) {
			checkNull("Table name:", table);
			if (!databases.containsKey(table)) {
				throw new IllegalArgumentException("The table:" + table
						+ " do not exist.");
			}
			return databases.get(table).delete(key, txn);
		}

		public void close() {
			for (BDBDatabase database : databases.values()) {
				database.close();
			}
		}
	}

	public byte[] getClassInfo(DbTransaction txn, int classId){
		return DataUtility.getClassInfo(classDb, classId, env, 20000);
	}
	
	public int getClassId(DbTransaction txn, byte[] data) {
		// TODO Auto-generated method stub
		return DataUtility.getClassId(classDb, data, env, 20000);
	}

}
