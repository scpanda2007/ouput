package viso.impl.util.lock;

/**
 * The result of attempting to request a lock.
 *
 * @param	<K> the type of key
 * @see		LockManager
 */
final class LockAttemptResult<K> {

	/** The lock request. */
	final LockRequest<K> request;

	/**
	 * A conflicting locker, if the request was not granted, or {@code
	 * null}.
	 */
	final Locker<K> conflict;

	/**
	 * The type of lock conflict, or {@code null} if and only if {@link
	 * #conflict} is {@code null}.
	 */
	final LockConflictType conflictType;

	/**
	 * Creates an instance of this class, with the specified conflict type.
	 * The {@code conflicct} and {@code conflictType} must either both be
	 * {@code null} or both not {@code null}.
	 *
	 * @param	request the lock request
	 * @param	conflict a conflicting locker or {@code null}
	 * @param	conflictType the conflict type or {@code null}
	 */
	LockAttemptResult(LockRequest<K> request, Locker<K> conflict,
			LockConflictType conflictType) {
		assert request != null;
		assert (conflict == null) == (conflictType == null);
		this.request = request;
		this.conflict = conflict;
		this.conflictType = conflictType;
	}

	/** Print fields, for debugging. */
	@Override
	public String toString() {
		return "LockAttemptResult["
				+ request
				+ ", conflict:"
				+ conflict
				+ (conflictType != null ? ", conflictType:" + conflictType : "")
				+ "]";
	}
}
