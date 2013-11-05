package viso.framework.service.protocol;


/**
 * An exception that indicates a login request failed.  The {@link
 * Throwable#getMessage getMessage} method returns a detail message
 * containing an explanation for the failure, the {@link #getReason
 * getReason} method returns the failure reason, and if the failure reason
 * is {@link FailureReason#OTHER}, the {@link Throwable#getCause getCause}
 * method returns the possibly-{@code null} cause of the failure.
 */
public class LoginFailureException extends Exception {

    /**
     * Reasons why a login fails.
     */
    public enum FailureReason {
	/** The server rejects a duplicate login. */
	DUPLICATE_LOGIN,
	/** The application rejects the login. */
	REJECTED_LOGIN,
	/** The server is temporarily unavailable. */
	SERVER_UNAVAILABLE,
	/** Other operational failure (see exception {@link
	 * Throwable#getCause cause} for detail). */
	OTHER
    };

    /** The serial version for this class. */
    private static final long serialVersionUID = 1L;

    /** The reason for the failure */
    private final FailureReason reason;

    /**
     * Constructs an instance with the specified detail {@code message}
     * and {@code reason}.
     *
     * @param	message a detail message, or {@code null}
     * @param	reason a failure reason
     */
    public LoginFailureException(String message, FailureReason reason) {
	super(message);
	if (reason == null) {
	    throw new NullPointerException("null reason");
	}
	this.reason = reason;
    }
    
    /**
     * Constructs an instance with the specified detail {@code message}
     * and {@code cause}.
     *
     * @param	message a detail message, or {@code null}
     * @param	cause the cause of this exception, or {@code null}
     */
    public LoginFailureException(String message, Throwable cause)
    {
	super(message, cause);
	this.reason = FailureReason.OTHER;
    }

    /**
     * Returns a failure reason.  If the returned reason is {@link
     * FailureReason#OTHER}, then the {@link Throwable#getCause cause} may
     * contain an exception that caused the failure.
     *
     * @return a failure reason
     */
    public FailureReason getReason() {
	return reason;
    }
}

