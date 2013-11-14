package viso.impl.framework.service.transaction;


/** Defines an interface for managing transactions. */
public interface TransactionCoordinator {

    /** The property used to specify the timeout value for transactions. */
    String TXN_TIMEOUT_PROPERTY = "com.sun.sgs.txn.timeout";

    /** The property used to specify the value for unbounded timeout. */
    String TXN_UNBOUNDED_TIMEOUT_PROPERTY =
	"com.sun.sgs.txn.timeout.unbounded";

    /** 
     * A property used to control whether we allow the prepareAndCommit
     * optimization, where the final participant has prepareAndCommit called
     * (one call) rather than prepare(), and at some later point commit().
     * <p>
     * The flag defaults to false.
     */
    String TXN_DISABLE_PREPAREANDCOMMIT_OPT_PROPERTY =
            "com.sun.sgs.txn.disable.prepareandcommit.optimization";
    /**
     * Creates a new transaction, and returns a handle for managing it.
     * If a timeout of {@link ScheduledTask#UNBOUNDED} is given, a
     * transaction will be created with the timeout value specified
     * by the property {@value #TXN_UNBOUNDED_TIMEOUT_PROPERTY}.
     *
     * @param timeout the timeout, in milliseconds, to be used for this
     *        transaction
     *
     * @return	a handle for managing the newly created transaction.
     */
    TransactionHandle createTransaction(long timeout);

    /**
     * Returns the default transaction timeout to use for bounded transactions.
     * This value is specified using the property
     * {@value #TXN_TIMEOUT_PROPERTY}
     *
     * @return the default transaction timeout
     */
    long getDefaultTimeout();
}