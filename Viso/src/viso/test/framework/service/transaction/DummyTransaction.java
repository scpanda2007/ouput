package viso.test.framework.service.transaction;

import viso.framework.service.Transaction;
import viso.framework.service.TransactionListener;
import viso.framework.service.TransactionParticipant;

public class DummyTransaction implements Transaction {

	@Override
	public void abort(Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkTimeout() {
		// TODO Auto-generated method stub

	}

	@Override
	public Throwable getAbortCause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isAborted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void join(TransactionParticipant participant) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerListener(TransactionListener listener) {
		// TODO Auto-generated method stub

	}

}
