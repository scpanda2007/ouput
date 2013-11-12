package viso.framework.profile;

import java.util.List;

import viso.framework.kernel.AccessedObject;


/**
 * An interface accessible through a {@code ProfileReport} that details the
 * collection of object accesses that occurred during the associated
 * transaction. If that transaction failed due to some kind of conflict, then
 * the type of failure and conflicting task is also identified.
 */
public interface AccessedObjectsDetail {

    /** Identifies a known type of conflict. */
    static enum ConflictType {
        /** Some access resulted in deadlock between transactions. */
        DEADLOCK,
        /**
         * Some requested access was not granted, e.g. due to timeout
         * or a lock being unavailable.
         */
        ACCESS_NOT_GRANTED,
        /** There was conflict, but the type of conflict is unknown. */
        UNKNOWN,
        /** There was no conflict caused by these object accesses. */
        NONE
    }

    /**
     * The collection of requested object accesses. The order is the same
     * as the order in which the objects were requested in the transaction.
     *
     * @return a {@code List} of {@code AccessedObject}s
     */
    List<AccessedObject> getAccessedObjects();

    /**
     * The type of conflict, if any, caused by these object accesses.
     *
     * @return the {@code ConflictType}
     */
    ConflictType getConflictType();

    /**
     * Returns the identifier of the transaction that caused this access to
     * fail, if any and if known.
     *
     * @return identifier for the successful transaction that caused conflict,
     *         or {@code null} if there was no conflict or the accessor
     *         is unknown
     */
    byte [] getConflictingId();

}
