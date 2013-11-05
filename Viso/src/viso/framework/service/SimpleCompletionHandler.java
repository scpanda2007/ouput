package viso.framework.service;

/**
 * A handler to be notified when operations for an associated
 * request are complete.
 *
 * @see ClientSessionStatusListener#prepareToRelocate(
 *		BigInteger,long,SimpleCompletionHandler)
 * @see IdentityRelocationListener#prepareToRelocate(
 *		Identity,long,SimpleCompletionHandler)
 * @see RecoveryListener#recover(Node,SimpleCompletionHandler)
 */
public interface SimpleCompletionHandler {

    /**
     * Notifies this handler that the operations initiated by the
     * request associated with this future are complete.  This
     * method is idempotent and can be called multiple times.
     */
    void completed();
}

