package viso.framework.kernel;

import java.util.Properties;

import viso.framework.service.Transaction;
import viso.framework.service.TransactionProxy;
import viso.impl.framework.kernel.AccessCoordinatorHandle;
import viso.impl.framework.profile.ProfileCollectorHandle;

/**
 * Define an {@link AccessCoordinatorHandle} that does not detect conflicts,
 * report accesses to the profiling system, or perform any error checking on
 * its arguments.
 */
public class NullAccessCoordinator implements AccessCoordinatorHandle,
		AccessReporter<Object> {
	/** Creates an instance of this class. */
	public NullAccessCoordinator() {
	}

	/**
	 * Creates an instance of this class, accepting the standard arguments for
	 * access coordinators, which are ignored.
	 *
	 * @param	properties the configuration properties
	 * @param	txnProxy the transaction proxy
	 * @param	profileCollectorHandle the profile collector handle
	 */
	public NullAccessCoordinator(Properties properties,
			TransactionProxy txnProxy,
			ProfileCollectorHandle profileCollectorHandle) {
	}

	/* -- Implement AccessCoordinatorHandle -- */

	/** {@inheritDoc} */
	public <T> AccessReporter<T> registerAccessSource(String sourceName,
			Class<T> objectIdType) {
		return (AccessReporter<T>) this;
	}

	/** {@inheritDoc} */
	public Transaction getConflictingTransaction(Transaction txn) {
		return null;
	}

	/** {@inheritDoc} */
	public void notifyNewTransaction(Transaction txn, long requestedStartTime,
			int tryCount) {
	}

	/* -- Implement AccessReporter -- */

	/** {@inheritDoc} */
	public void reportObjectAccess(Object objectId, AccessType type) {
	}

	/** {@inheritDoc} */
	public void reportObjectAccess(Transaction txn, Object objectId,
			AccessType type) {
	}

	/** {@inheritDoc} */
	public void reportObjectAccess(Object objectId, AccessType type,
			Object description) {
	}

	/** {@inheritDoc} */
	public void reportObjectAccess(Transaction txn, Object objectId,
			AccessType type, Object description) {
	}

	/** {@inheritDoc} */
	public void setObjectDescription(Object objectId, Object description) {
	}

	/** {@inheritDoc} */
	public void setObjectDescription(Transaction txn, Object objectId,
			Object description) {
	}
}
