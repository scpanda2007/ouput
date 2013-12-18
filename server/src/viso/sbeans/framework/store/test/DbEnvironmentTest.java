package viso.sbeans.framework.store.test;

import java.util.Arrays;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;

import viso.sbeans.framework.store.db.BDBDatabase;
import viso.sbeans.framework.store.db.DbEnvironment;
import viso.sbeans.framework.store.db.DbTransaction;


public class DbEnvironmentTest {
	
	static DbEnvironment env;
	
	@BeforeClass
	public static void beforeAll(){
		env = DbEnvironment.environment("D:", "sbeans.store");
	}
	
	@AfterClass
	public static void afterAll(){
		env.close();
	}
	
	@Before
	public void setUp(){}
	
	@After
	public void tearDown(){}
	
	@Test
	public void testOpenDb(){
		DbTransaction txn = env.beginTransaction(1);
		BDBDatabase store = env.open(txn, "testOpenDb", true);
		txn.commit();
		store.close();		
	}
	
	@Test
	public void testBaseFunc(){
		DbTransaction txn = env.beginTransaction(1);
		BDBDatabase store = env.open(txn, "testBaseFunc", true);
		byte[] key = new byte[]{0,1,2,3,4,5,6};
		byte[] data = new byte[]{0,1,2,3,4,5,6};
		store.put(key, data, txn);
		byte[] data0 = store.get(key, txn, false);
		System.out.println(""+Arrays.toString(data0));
		byte[] data1 = store.testPartial(key, txn, 1, 2, true);
		System.out.println(""+Arrays.toString(data1));
		byte[] data2 = store.testPartial(key, txn, 1, 2, false);
		System.out.println(""+Arrays.toString(data2));
		byte[] data3 = store.testPartial(key, txn, 1, 10, true);
		System.out.println(""+Arrays.toString(data3));
		store.delete(key, txn);
		txn.commit();
		store.close();
	}
	
}
