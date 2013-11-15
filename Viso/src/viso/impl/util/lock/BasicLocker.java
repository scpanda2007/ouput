package viso.impl.util.lock;

/**
 * Records information about an entity that requests locks from a {@link
 * LockManager} and that permits only a single active lock request.
 *
 * @param	<K> the type of key
 */
public class BasicLocker<K> extends Locker<K> {

	/**
	 * The result of the lock request that this locker is waiting for, or
	 * {@code null} if it is not waiting.  Synchronize on this locker when
	 * accessing this field.
	 */
	private LockAttemptResult<K> waitingFor;

	/* -- Constructor -- */

	/**
	 * Creates an instance of this class.
	 *
	 * @param	lockManager the lock manager for this locker
	 */
	public BasicLocker(LockManager<K> lockManager) {
		super(lockManager);
	}

	/* -- Package access methods -- */

	/**
	 * {@inheritDoc} <p>
	 *
	 * This implementation returns the lock attempt request associated with
	 * this locker, if any.
	 */
	@Override
	LockAttemptResult<K> getWaitingFor() {
		assert lockManager.checkAllowLockerSync(this);
		synchronized (this) {
			return waitingFor;
		}
	}

	/**
	 * {@inheritDoc} <p>
	 *
	 * This implementation sets the lock attempt request associated with this
	 * locker.
	 *
	 * @throws	IllegalArgumentException {@inheritDoc}
	 */
	@Override
	void setWaitingFor(LockAttemptResult<K> waitingFor) {
		assert lockManager.checkAllowLockerSync(this);
		if (waitingFor != null && waitingFor.conflict == null) {
			throw new IllegalArgumentException(
					"Attempt to specify a lock attempt result that is not a"
							+ " conflict: " + waitingFor);
		}
		synchronized (this) {
			this.waitingFor = waitingFor;
		}
	}
}
