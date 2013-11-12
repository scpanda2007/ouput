package viso.impl.framework.auth;

import java.io.Serializable;

import viso.framework.auth.Identity;


/**
 * This is a basic implementation of <code>Identity</code> that maps a name
 * to the identity.
 */
public class IdentityImpl implements Identity, Serializable
{
    private static final long serialVersionUID = 1L;

    // the name of this identity
    private final String name;

    /**
     * Creates an instance of <code>Identity</code> associated with the
     * given name.
     *
     * @param name the name of this identity
     */
    public IdentityImpl(String name) {
        if (name == null) {
            throw new NullPointerException("Null names are not allowed");
        }

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void notifyLoggedIn() {

    }

    /**
     * {@inheritDoc}
     */
    public void notifyLoggedOut() {

    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if ((o == null) || (!(o instanceof IdentityImpl))) {
            return false;
        }
        return ((IdentityImpl) o).name.equals(name);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
	return getClass().getName() + "[" + name + "]";
    }
}
