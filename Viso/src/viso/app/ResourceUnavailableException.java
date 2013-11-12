package viso.app;

/**
 * Thrown when an operation fails because a resource, necessary for
 * the operation, is unavailable.
 */
public class ResourceUnavailableException extends RuntimeException implements
		ExceptionRetryStatus {
	/** The version of the serialized form. */
	private static final long serialVersionUID = 1;

	/**
	 * Creates an instance of this class with the specified detail message.
	 *
	 * @param	message the detail message or <code>null</code>
	 */
	public ResourceUnavailableException(String message) {
		super(message);
	}

	/**
	 * Creates an instance of this class with the specified detail message and
	 * cause.
	 *
	 * @param	message the detail message or <code>null</code>
	 * @param	cause the cause or <code>null</code>
	 */
	public ResourceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	/* -- Implement ExceptionRetryStatus -- */

	/**
	 * {@inheritDoc} <p>
	 *
	 * This implementation always returns <code>true</code>.
	 */
	public boolean shouldRetry() {
		return true;
	}
}
