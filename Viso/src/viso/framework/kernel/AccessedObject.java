package viso.framework.kernel;

import viso.framework.kernel.AccessReporter.AccessType;


/**
 * An interface that provides details of a single object access. Two accessed
 * objects are identical if both were reported by an {@link AccessReporter}
 * obtained by registering an access source with a single {@link
 * AccessCoordinator} using the same source name, and the values returned by
 * {@link #getObjectId()} and {@link #getAccessType()} are equal.
 */
public interface AccessedObject {

    /**
     * Returns the identifier for the accessed object.
     *
     * @return the identifier for the accessed object
     */
    Object getObjectId();

    /**
     * Returns the type of access requested.
     *
     * @return the {@code AccessType}
     */
    AccessType getAccessType();

    /**
     * Returns the supplied description of the object, if any.
     *
     * @return the associated description, or {@code null}
     *
     * @see AccessReporter#setObjectDescription(Object,Object)
     */
    Object getDescription();

    /**
     * Returns the name of the source that reported this object access.
     *
     * @return the object's source
     */
    String getSource();

}
