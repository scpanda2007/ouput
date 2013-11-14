package viso.impl.framework.kernel;

import viso.framework.kernel.AccessCoordinator;
import viso.framework.service.Transaction;

/**
 * A subinterface of {@code AccessCoordinator} that the kernel uses to notify
 * the access coordinator of new transactions.
 */
public interface AccessCoordinatorHandle extends AccessCoordinator {
    
    /** 
     * Notifies the coordinator that a new transaction is starting. 
     *
     * @param	txn the transaction
     * @param	requestedStartTime the time in milliseconds that the task
     *		associated with the transaction was originally requested to
     *		start
     * @param	tryCount the number of times that transactions have been
     *		attempted for the task associated with {@code txn}
     * @throws	IllegalArgumentException if {@code requestedStartTime} is less
     *		than {@code 0} or {@code tryCount} is less than {@code 1}
     * @throws	IllegalStateException if this transaction has already been
     *		started
     */
    void notifyNewTransaction(
	Transaction txn, long requestedStartTime, int tryCount);
}
