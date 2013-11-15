package viso.impl.util.lock;

/** The type of a lock conflict detected by a {@link LockManager}. */
public enum LockConflictType {

    /** The request is currently blocked. */
    BLOCKED,

    /** The request timed out. */
    TIMEOUT,

    /** The request was denied. */
    DENIED,

    /** The request was interrupted. */
    INTERRUPTED,

    /** The request resulted in deadlock and was chosen to be aborted. */
    DEADLOCK;
}