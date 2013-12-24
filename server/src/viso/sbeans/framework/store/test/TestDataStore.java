package viso.sbeans.framework.store.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import viso.sbeans.framework.kernel.TaskScheduler;
import viso.sbeans.framework.kernel.ThreadContext;
import viso.sbeans.framework.service.data.DataService;
import viso.sbeans.framework.store.DataStore;
import viso.sbeans.framework.store.data.DataObjectReference;
import viso.sbeans.framework.transaction.VTransaction;

public class TestDataStore {
	
	static final String TableName = "TestDataStore";
	
	@BeforeClass
	static public void beforeAll(){
		Set<String> tables = new HashSet<String>();
		tables.add(TableName);
		DataService.createDataService(new DataStore(null).init("D:", "TestC", tables));
	}
	
	@AfterClass
	static public void afterAll(){
		if(DataService.getInstance()!=null){
			DataService.getInstance().shutdown();
		}
	}
	
	@Test
	public void testDataStore() {
		VTransaction txn = VTransaction.createTransaction();
		try {
			ThreadContext.setTransaction(txn);
			for (int i = 10000-1; i >= 0; i--) {
				DataObjectReference<?> ref = DataService.getInstance()
						.getReference(TableName + "." + i, false);
				if (ref == null) {
					System.out.println("try put...");
					TableClass3 object = new TableClass3(1, 2, i);
					ref = DataService.getInstance().addManaged(
							TableName + "." + i, object);
				}
			}
			txn.commit();
		} finally {
			ThreadContext.clearTransaction(txn);
		}//查询1.0 s 搜索0.6s
	}
	
	@Test
	public void testRemove(){
		VTransaction txn = VTransaction.createTransaction();
		try {
			ThreadContext.setTransaction(txn);
			for (int i = 0; i < 10000; i++) {
				DataObjectReference<?> ref = DataService.getInstance()
						.getReference(TableName + "." + i, false);
				if (ref != null) {
					ref.delete();
//					TableClass3 object = (TableClass3)ref.get(false);
//					System.out.println("try delete..."+object.toString());
//					DataService.getInstance().removeObject(object);
				}
			}
			txn.commit();
		} finally {
			ThreadContext.clearTransaction(txn);
		}//4.0s
	}
	
	@Test
	public void testModifyObject(){
		// TODO Auto-generated method stub
		boolean commit = false;
		VTransaction txn = VTransaction.createTransaction();
		try {
			ThreadContext.setTransaction(txn);
			DataObjectReference<?> ref0 = DataService.getInstance()
					.getReference(TableName + "." + "4", true);
			TableClass3 object0 = (TableClass3)ref0.get(true);
			System.out.println("111"+object0.toString());
			object0.b = -500;
			object0.cl4.d = -900;
			DataService.getInstance().setDirty(object0);
			txn.commit();
			commit = true;
		}  finally {
			if (!commit) {
				txn.abort(new Exception("failed----------"));
			}
		}
	}
	
	@Test
	public void testDeadLock() {
		TaskScheduler scheduler = new TaskScheduler();
		scheduler.sumbit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean commit = false;
				System.out.println("current thread->"+Thread.currentThread().getName());
				VTransaction txn = VTransaction.createTransaction();
				try {
					ThreadContext.setTransaction(txn);
					DataObjectReference<?> ref0 = DataService.getInstance()
							.getReference(TableName + "." + "0", false);
					System.out.println("1 get lock "+0);
					Thread.sleep(2000);
					DataObjectReference<?> ref1 = DataService.getInstance()
					.getReference(TableName + "." + "1", true);
					System.out.println("1 get lock "+1);
					TableClass3 object0 = (TableClass3)ref0.get(false);
					TableClass3 object1 = (TableClass3)ref1.get(true);
					object0.b = -500;
					object1.b = -600;
					DataService.getInstance().setDirty(object0);
					DataService.getInstance().setDirty(object1);
					txn.commit();
					System.out.println("1 commit ");
					commit = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (!commit) {
						txn.abort(new Exception("failed----------"));
					}
				}
			}	
		});
		scheduler.sumbit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean commit = false;
				VTransaction txn = VTransaction.createTransaction();
				System.out.println("current thread->"+Thread.currentThread().getName());
				try {
					ThreadContext.setTransaction(txn);
					DataObjectReference<?> ref1 = DataService.getInstance()
							.getReference(TableName + "." + "1", false);
					System.out.println("2 get lock "+1);
					Thread.sleep(1000);
					DataObjectReference<?> ref0 = DataService.getInstance()
					.getReference(TableName + "." + "0", true);
					TableClass3 object0 = (TableClass3)ref0.get(true);
					TableClass3 object1 = (TableClass3)ref1.get(false);
					System.out.println("2 get lock "+0);
					object0.b = 500;
					object1.b = 600;
					DataService.getInstance().setDirty(object0);
					DataService.getInstance().setDirty(object1);
					txn.commit();
					commit = true;
					System.out.println("2 commit ");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (!commit) {
						txn.abort(new Exception("failed----------"));
					}
				}
			}	
		});
		try {
			Thread.sleep(4500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//实时上没有发生死锁
	}
	
	@Test
	public void testDeadLock2() {
		TaskScheduler scheduler = new TaskScheduler();
		scheduler.sumbit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean commit = false;
				VTransaction txn = VTransaction.createTransaction();
				try {
					ThreadContext.setTransaction(txn);
					DataObjectReference<?> ref0 = DataService.getInstance()
							.getReference(TableName + "." + "0", true);
					Thread.sleep(500);
					DataObjectReference<?> ref1 = DataService.getInstance()
					.getReference(TableName + "." + "1", true);
					txn.commit();
					commit = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (!commit) {
						txn.abort(new Exception("failed----------"));
					}
				}
			}	
		});
		scheduler.sumbit(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean commit = false;
				VTransaction txn = VTransaction.createTransaction();
				try {
					ThreadContext.setTransaction(txn);
					DataObjectReference<?> ref1 = DataService.getInstance()
							.getReference(TableName + "." + "1", true);
					Thread.sleep(1500);
					DataObjectReference<?> ref0 = DataService.getInstance()
					.getReference(TableName + "." + "0", true);
					txn.commit();
					commit = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (!commit) {
						txn.abort(new Exception("failed----------"));
					}
				}
			}	
		});
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//实时上没有发生死锁
	}
}	
