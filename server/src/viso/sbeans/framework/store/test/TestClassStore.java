package viso.sbeans.framework.store.test;

import static org.junit.Assert.assertArrayEquals;

import java.util.HashSet;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import viso.sbeans.framework.store.DataStore;

public class TestClassStore {
	
	@Test
	public void testClassStoreOpen(){
		DataStore test = new DataStore(null);
		test.init("D:", "testA", new HashSet<String>());
		test.shutdown();
	}
	
	DataStore store;
	
	@Before
	public void setUp(){
		store = new DataStore(null);
		store.init("D:", "testB", new HashSet<String>());
	}
	
	@Test
	public void fetchAClassOstreamClass(){
		byte[] nonsense = new byte[]{0,1,2,3,4,5,6};
		int id = store.getClassId(null, nonsense);
		byte[] getFromClassDb = store.getClassInfo(null, id);
		assertArrayEquals(nonsense,getFromClassDb);
	}
	
	@After
	public void tearDown(){
		if(store!=null)store.shutdown();
	}
	
}
