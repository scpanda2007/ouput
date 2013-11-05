package viso.framework.auth;


/**
 * Represents credentials that can be used for authentication. These
 * credentials may be consumed by any mechanism for authentication.
 * Implementations of <code>IdentityCredentials</code> should not
 * actually contain any authentication logic. This should instead be
 * part of the consuming <code>IdentityAuthenticator</code>.
 */
public interface IdentityCredentials
{

    /**
     * Returns the identifier for the type of credentials. This will be
     * used by the <code>IdentityManager</code> to find applicable
     * <code>IdentityAuthenticator</code>s to consume these credentials.
     *
     * @return an identifier for the type of credentials
     */
    String getCredentialsType();

}