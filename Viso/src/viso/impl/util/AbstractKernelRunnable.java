package viso.impl.util;

import viso.framework.kernel.KernelRunnable;

/**
 * A utility class that implements the {@code getBaseTaskType} method of
 * {@code KernelRunnable} to return the class name of the concrete instance.
 */
public abstract class AbstractKernelRunnable implements KernelRunnable {

	private final String name;

	/**
	 * Constructs an instance with the specified {@code name}. If the
	 * {@code name} is non-{@code null}, then it is included in the
	 * {@code toString} method to identify the instance.
	 * 
	 * @param name
	 *            a name for the instance
	 */
	public AbstractKernelRunnable(String name) {
		this.name = name;
	}

	/**
	 * Returns the class name of the concrete instance.
	 * 
	 * @return the class name of the concrete instance
	 */
	public String getBaseTaskType() {
		return toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		String nameString = name != null ? "[" + name + "]" : "";
		return getClass().getName() + nameString;
	}
}
