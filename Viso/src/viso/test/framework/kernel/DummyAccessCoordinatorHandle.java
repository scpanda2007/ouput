package viso.test.framework.kernel;

import viso.framework.kernel.AccessReporter;
import viso.framework.service.Transaction;
import viso.impl.framework.kernel.AccessCoordinatorHandle;

public class DummyAccessCoordinatorHandle implements AccessCoordinatorHandle{

	public DummyAccessCoordinatorHandle(){}
	
	@Override
	public void notifyNewTransaction(Transaction txn, long requestedStartTime,
			int tryCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transaction getConflictingTransaction(Transaction txn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> AccessReporter<T> registerAccessSource(String sourceName,
			Class<T> objectIdType) {
		// TODO Auto-generated method stub
		return null;
	}

}
