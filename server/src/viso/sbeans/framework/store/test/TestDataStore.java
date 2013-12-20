package viso.sbeans.framework.store.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

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
				System.out.println("" + ref.get(false).toString());
			}
			txn.commit();
		} finally {
			ThreadContext.clearTransaction(txn);
		}//²éÑ¯1.0 s ËÑË÷0.6s
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
					TableClass3 object = (TableClass3)ref.get(false);
					System.out.println("try delete..."+object.toString());
					DataService.getInstance().removeObject(object);
				}
			}
			txn.commit();
		} finally {
			ThreadContext.clearTransaction(txn);
		}//4.0s
	}
}	
