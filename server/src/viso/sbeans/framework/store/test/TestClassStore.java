package viso.sbeans.framework.store.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.store.DataEncoder;
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
	public void fetchAClassOstreamClass() {
		int requestNum = 10;
		ExecutorService service = Executors.newScheduledThreadPool(requestNum, new NamedThreadFactory("fetchAClassOstreamClass"));
		
		List<Future<?>> setResults = new ArrayList<Future<?>>();
		
		for(int i=0;i<requestNum;i++){
			
			final int index = i;
			
			Future<?> wait = service.submit(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					runSet(index);
				}
				
			});
			
			setResults.add(wait);
		}

		try {
			for (Future<?> wait : setResults) {
				wait.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Future<?>> getResults = new ArrayList<Future<?>>();
		
		for(int i=0;i<requestNum;i++){
			
			final int index = i;
			
			Future<?> wait = service.submit(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					runGet(index);
				}
				
			});
			
			getResults.add(wait);
		}
		
		try {
			for (Future<?> wait : getResults) {
				wait.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		service.shutdown();
	}
	
	public void runGet(int index){
		long time = System.currentTimeMillis();
		for (int i = 10000; i >= 0; i--) {
			store.getClassInfo(null, i);
		}//逆向查找 0.052s
		long endtime = System.currentTimeMillis();
		System.out.println("["+index+"] End store class id:"+endtime);
		System.out.println("["+index+"] time take : "+new Double(endtime - time)/1000+" s");
	}
	
	public void runSet(int index){
		long time = System.currentTimeMillis();
		for (int i = 0; i <= 10000; i++) {
			byte[] nonsense = DataEncoder.encodeInt(i, 17);
			store.getClassId(null, nonsense);
		}
		long endtime = System.currentTimeMillis();
		System.out.println("["+index+"] time take : "+new Double(endtime - time)/1000+" s");//顺向查找 0.087s 顺向插入 0.346s
		
	}
	
	@After
	public void tearDown(){
		if(store!=null)store.shutdown();
	}
	
}
