package viso.framework.service;

import viso.app.TransactionAbortedException;

/**
 * Thrown when an operation fails because it was interrupted while it was
 * blocked waiting for another transaction.
 */
public class TransactionInterruptedException extends
		TransactionAbortedException {
	/** The version of the serialized form. */
	private static final long serialVersionUID = 1;

	/**
	 * Creates an instance of this class with the specified detail message.
	 *
	 * @param	message the detail message or <code>null</code>
	 */
	public TransactionInterruptedException(String message) {
		super(message);
	}

	/**
	 * Creates an instance of this class with the specified detail message and
	 * cause.
	 *
	 * @param	message the detail message or <code>null</code>
	 * @param	cause the cause or <code>null</code>
	 */
	public TransactionInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}
}