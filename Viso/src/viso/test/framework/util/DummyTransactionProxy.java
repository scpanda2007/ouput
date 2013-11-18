package viso.test.framework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import viso.app.TransactionNotActiveException;
import viso.framework.auth.Identity;
import viso.framework.kernel.schedule.ScheduledTask;
import viso.framework.service.Service;
import viso.framework.service.Transaction;
import viso.framework.service.TransactionProxy;
import viso.impl.framework.service.transaction.TransactionCoordinator;
import viso.impl.framework.service.transaction.TransactionHandle;
import viso.test.framework.util.DummyTransaction.UsePrepareAndCommit;

import static viso.impl.framework.service.transaction.TransactionCoordinatorImpl.BOUNDED_TIMEOUT_DEFAULT;
import static viso.impl.framework.service.transaction.TransactionCoordinatorImpl.UNBOUNDED_TIMEOUT_DEFAULT;

/**
 * Provides a simple implementation of {@code TransactionProxy} and {@code
 * TransactionCoordinator}, for testing.
 */
public class DummyTransactionProxy implements TransactionCoordinator,
		TransactionProxy {
	/** The value for bounded timeout. */
	private long boundedTimeout;

	/** The value for unbounded timeout. */
	private long unboundedTimeout;

	/** Should we use {@code prepareAndCommit} or separate calls? */
	private boolean disablePrepareAndCommitOpt;

	/** Stores information about the transaction for the current thread. */
	private final ThreadLocal<DummyTransaction> threadTxn = new ThreadLocal<DummyTransaction>();

	/** The task owner. */
	private final Identity taskOwner = new DummyIdentity();

	/** Mapping from type to service. */
	private final Map<Class<? extends Service>, Service> services = new HashMap<Class<? extends Service>, Service>();

	/** Creates an instance of this class. */
	public DummyTransactionProxy() {
		Properties properties = System.getProperties();
		boundedTimeout = Long.parseLong(properties.getProperty(
				TXN_TIMEOUT_PROPERTY, String.valueOf(BOUNDED_TIMEOUT_DEFAULT)));
		unboundedTimeout = Long.parseLong(properties.getProperty(
				TXN_UNBOUNDED_TIMEOUT_PROPERTY, String
						.valueOf(UNBOUNDED_TIMEOUT_DEFAULT)));
		disablePrepareAndCommitOpt = Boolean
				.parseBoolean(properties.getProperty(
						TXN_DISABLE_PREPAREANDCOMMIT_OPT_PROPERTY, "false"));
	}

	/* -- Implement TransactionCoordinator -- */

	public TransactionHandle createTransaction(long timeout) {
		if (timeout == ScheduledTask.UNBOUNDED) {
			timeout = unboundedTimeout;
		} else if (timeout <= 0) {
			throw new IllegalArgumentException(
					"Timeout value must be greater than 0: " + timeout);
		}
		return new TxnHandle(disablePrepareAndCommitOpt, timeout);
	}

	public long getDefaultTimeout() {
		return boundedTimeout;
	}

	private static final class TxnHandle implements TransactionHandle {
		private final DummyTransaction txn;

		TxnHandle(boolean disablePrepareAndCommitOpt, long timeout) {
			txn = new DummyTransaction(
					disablePrepareAndCommitOpt ? UsePrepareAndCommit.NO
							: UsePrepareAndCommit.ARBITRARY);
		}

		public Transaction getTransaction() {
			return txn;
		}

		public void commit() throws Exception {
			txn.commit();
		}
	}

	/* -- Implement TransactionProxy -- */

	public Transaction getCurrentTransaction() {
		Transaction txn = threadTxn.get();
		if (txn == null) {
			throw new TransactionNotActiveException("No transaction is active");
		}
		txn.checkTimeout();
		return txn;
	}

	public boolean inTransaction() {
		Transaction txn = threadTxn.get();
		return (txn != null);
	}

	public Identity getCurrentOwner() {
		return taskOwner;
	}

	public <T extends Service> T getService(Class<T> type) {
		Object service = services.get(type);
		if (service == null) {
			throw new MissingResourceException("Service of type " + type
					+ " was not found", type.getName(), "Service");
		}
		return type.cast(service);
	}

	/* -- Other public methods -- */

	/**
	 * Specifies the transaction object that will be returned for the current
	 * transaction, or null to specify that no transaction should be
	 * associated.  Also stores itself in the transaction instance, so that the
	 * transaction can clear the current transaction on prepare, commit, or
	 * abort.
	 */
	public void setCurrentTransaction(DummyTransaction txn) {
		threadTxn.set(txn);
		if (txn != null) {
			txn.proxy = this;
		}
	}

	/**
	 * Specifies the service that should be returned for an exact match for
	 * the specified type.
	 */
	public <T extends Service> void setComponent(Class<T> type, T service) {
		if (type == null || service == null) {
			throw new NullPointerException("Arguments must not be null");
		}
		services.put(type, service);
	}

	/**
	 * Sets the bounded transaction timeout for creating new transactions.
	 *
	 * @param	timeout the bounded transaction timeout
	 */
	public void setBoundedTransactionTimeout(long timeout) {
		boundedTimeout = timeout;
	}

	/**
	 * Sets the unbounded transaction timeout for creating new transactions.
	 *
	 * @param	timeout the bounded transaction timeout
	 */
	public void setUnboundedTransactionTimeout(long timeout) {
		unboundedTimeout = timeout;
	}

	/**
	 * Sets whether new transactions should use {@code prepareAndCommit}.
	 *
	 * @param	disablePrepareAndCommitOpt whether use of {@code
	 *		prepareAndCommit} should be disabled
	 */
	public void setDisablePrepareAndCommitOpt(boolean disable) {
		disablePrepareAndCommitOpt = disable;
	}
}
