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
		 * ��ʼ�����ݿ�
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
		 * ���ĳ�����ĳ������
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
		 * �Բ����ǵķ�ʽ����д��һ��ֵ�������ظü�ֵ��ֵ�Ƿ񱾲����ڣ���������Ƿ�д��ɹ�ȡ�������ݿ��������Ƿ�����ͬһ��ֵ�� ���ֵ
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
		 * д��һ��ֵ
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
		 * ɾ��һ��ֵ �������Ƿ��������һ��ֵ
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
