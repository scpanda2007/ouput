package viso.impl.framework.auth;

import viso.framework.auth.IdentityCredentials;


/**
 * This simple implementation of <code>IdentityCredentials</code> is used to
 * represent a name and password pair.
 */
public class NamePasswordCredentials implements IdentityCredentials
{

    /**
     * The identifier for this type of credentials.
     */
    public static final String TYPE_IDENTIFIER = "NameAndPasswordCredentials";

    // the name and password
    private final String name;
    private final char [] password;

    /**
     * Creates an instance of <code>NamePasswordCredentials</code>.
     *
     * @param name the name
     * @param password the password
     */
    public NamePasswordCredentials(String name, char [] password) {
        this.name = name;
        this.password = password.clone();
    }

    /**
     * {@inheritDoc}
     */
    public String getCredentialsType() {
        return TYPE_IDENTIFIER;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public char [] getPassword() {
        return password.clone();
    }

}
