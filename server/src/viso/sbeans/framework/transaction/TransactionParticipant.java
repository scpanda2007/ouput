package viso.sbeans.framework.transaction;

public interface TransactionParticipant {
	public void commit(VTransaction transaction);
	public void abort(VTransaction transaction);
}
