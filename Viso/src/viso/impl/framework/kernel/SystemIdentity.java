package viso.impl.framework.kernel;

import viso.impl.framework.auth.IdentityImpl;

/**
 * The system identity is pinned to the node it was created on and
 * is not used for load balancing decisions.
 */
public class SystemIdentity extends IdentityImpl {

    private static final long serialVersionUID = 1L;
    
    /**
     * Creates an instance of {@code SystemIdentity} associated with the
     * given name.
     *
     * @param name the name of this identity
     */
    public SystemIdentity(String name) {
        super(name);
    }
}