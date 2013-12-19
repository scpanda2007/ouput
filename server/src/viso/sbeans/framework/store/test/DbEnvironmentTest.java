package viso.sbeans.framework.store.test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;

import viso.com.util.NamedThreadFactory;
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
	public void testTxnConflit(){
		final DbTransaction txn1 = env.beginTransaction(1000);
		final BDBDatabase store = env.open(txn1, "testTxnConflit", true);
		txn1.commit();
		ExecutorService executor = Executors.newScheduledThreadPool(2, new NamedThreadFactory("testTxnConflit"));
		Future<?> wait0 = executor.submit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final DbTransaction txn2 = env.beginTransaction(1000);
				try{
					store.put(new byte[]{0,1}, new byte[1], txn2);
					Thread.sleep(500);
					txn2.commit();
					System.out.println("hehe");
				}catch(Exception e){
					txn2.abort();
					System.out.println("xxx");
				}
			}
		});
		
		Future<?> wait1 = executor.submit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final DbTransaction txn3 = env.beginTransaction(1000);
				try{
					store.put(new byte[]{0,1}, new byte[1], txn3);
					Thread.sleep(500);
					txn3.commit();
					System.out.println("aasdf");
				}catch(Exception e){
					txn3.abort();
					System.out.println("yyy");
				}
			}
		});
		
		try {
			wait0.get();
			wait1.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdown();
		
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
