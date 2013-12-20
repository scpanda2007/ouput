package viso.sbeans.framework.store.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.store.DataEncoder;
import viso.sbeans.framework.store.DataStore;
import viso.sbeans.framework.store.data.ClassSerializer;
import viso.sbeans.framework.store.data.ClassTables;
import viso.sbeans.framework.store.data.DataObject;
import viso.sbeans.framework.store.data.SerialUtil;

class TableClass0 {
	public List<Integer> array0 = new ArrayList<Integer>();
	public Map<Integer,String> map0 = new HashMap<Integer,String>();
	int b = 1;
	public String toString(){
		return "b:"+1;
	}
}

class TableClass1 extends TableClass0{
	public Map<Integer,String> map1 = new TreeMap<Integer,String>();
	int b = 2;
	public String toString(){
		return "b:"+2;
	}
}

class TableClass2 extends TableClass1 implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public TableClass2(int b){
		this.b = b;
	}
	public String toString(){
		return "b:"+b;
	}
	
}

class TableClass3 extends TableClass2 implements DataObject{

	int c;
	
	TableClass4 cl4;
	
	Map<String,String> tt;
	
	public TableClass3(int b,int c,int d) {
		super(b);
		// TODO Auto-generated constructor stub
		this.c = c;
		cl4 = new TableClass4();
		cl4.d = d;
		tt = new HashMap<String,String>();
	}

	private static final long serialVersionUID = 1L;
	
	public String toString(){
		return "b:"+b+" c:"+c+" "+cl4.toString();
	}
}

class TableClass4 implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public int d = 1;
	
	public String toString(){
		return "d:"+d;
	}
}

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
	public void testClassTables() {

		TaskScheduler scheduler = new TaskScheduler();
		for (int i = 0; i < 4; i++){
			
			final int time = i*1000;
			
			scheduler.sumbit(new Runnable() {

				@Override
				public void run() {
					try{
						Thread.sleep(time);
					}catch(InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// TODO Auto-generated method stub
					ClassTables tables = new ClassTables(store);
					ClassSerializer serializer = tables
							.createClassSerializer(null);
					TableClass3 obj3 = new TableClass3(4, 5, 6);
					byte[] byte0 = SerialUtil.write(obj3, serializer);
					System.out.println(Arrays.toString(byte0));
					TableClass3 obj4 = SerialUtil.read(byte0, serializer);
					System.out.println(obj4.toString());
					System.gc();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
		}
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scheduler.shutdown();
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
