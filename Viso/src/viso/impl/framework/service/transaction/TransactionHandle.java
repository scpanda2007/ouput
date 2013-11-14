package viso.impl.framework.service.transaction;

import viso.framework.service.Transaction;


/** Defines an interface for managing a transaction. */
public interface TransactionHandle {

    /**
     * Returns the transaction associated with this handle.
     *
     * @return	the transaction
     */
    Transaction getTransaction();

    /**
     * Prepares and commits the transaction associated with this handle. <p>
     *
     * If the transaction has been aborted, or when preparing a transaction
     * participant or calling {@link TransactionListener#beforeCompletion
     * beforeCompletion} on a transaction listener aborts the transaction
     * without throwing an exception, then the exception thrown will have as
     * its cause the value provided in the first call to {@link
     * Transaction#abort abort} on the transaction, if any.  If the cause
     * implements {@link ExceptionRetryStatus}, then the exception thrown will,
     * too, and its {@link ExceptionRetryStatus#shouldRetry shouldRetry} method
     * will return the value returned by calling that method on the cause.  If
     * no cause was supplied, then {@code shouldRetry} will either not
     * implement {@code ExceptionRetryStatus} or its {@code shouldRetry} method
     * will return {@code false}.
     *
     * @throws	TransactionNotActiveException if the transaction has been
     *		aborted
     * @throws	TransactionAbortedException if a call to {@link
     *		TransactionParticipant#prepare prepare} on a transaction
     *		participant or to {@code beforeCompletion} on a transaction
     *		listener aborts the transaction but does not throw an exception
     * @throws	IllegalStateException if {@code prepare} has been called on any
     *		transaction participant and {@link Transaction#abort abort} has
     *		not been called on the transaction, or if called from a thread
     *		that is not the thread that created the transaction
     * @throws	Exception any exception thrown when calling {@code prepare} on
     *		a participant or {@code beforeCompletion} on a listener
     */
    void commit() throws Exception;
}
