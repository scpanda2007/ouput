package viso.test.framework.util;

import java.lang.reflect.Method;
import java.util.Properties;

import viso.framework.service.store.db.DbEnvironment;
import viso.impl.framework.service.data.store.DataStoreImpl;
import viso.impl.framework.service.data.store.bdb.BdbEnvironment;

/** Utilities for handling the data store database layer in tests. */
public final class UtilDataStoreDb {

	/** Types of data store database environment implementations. */
	public enum EnvironmentType {

		/** Berkeley DB Standard Edition */
		BDB,

		/** Berkeley DB Java Edition */
		JE
	};

	/**
	 * Returns type of data store database environment implementation in use.
	 * 
	 * @param properties
	 *            the configuration properties
	 * @return the database environment type
	 */
	public static EnvironmentType getEnvironmentType(Properties properties) {
		String className = properties
				.getProperty(DataStoreImpl.ENVIRONMENT_CLASS_PROPERTY);
		if (className == null
				|| className
						.equals("com.sun.sgs.impl.service.data.store.db.bdb.BdbEnvironment")) {
			return EnvironmentType.BDB;
		} else if (className
				.equals("com.sun.sgs.impl.service.data.store.db.je."
						+ "JeEnvironment")) {
			return EnvironmentType.JE;
		} else {
			throw new RuntimeException("Unknown environment class: "
					+ className);
		}
	}

	/**
	 * Returns the system property that specifies the lock timeout.
	 * 
	 * @param properties
	 *            the configuration properties
	 * @return the system property for specifying the lock timeout
	 */
	public static String getLockTimeoutPropertyName(Properties properties) {
		switch (getEnvironmentType(properties)) {
		case BDB:
			return BdbEnvironment.LOCK_TIMEOUT_PROPERTY;
		case JE:
			throw new RuntimeException("not implement yet");
//			return JeEnvironment.LOCK_TIMEOUT_PROPERTY;
		default:
			throw new RuntimeException("Unknown environment");
		}
	}

	/**
	 * Returns the lock timeout in microseconds for the specified data store
	 * database environment.
	 * 
	 * @param env
	 *            the database environment
	 * @return the lock timeout in microseconds
	 */
	public static long getLockTimeoutMicros(DbEnvironment env) {
		Method method = UtilReflection.getMethod(env.getClass(),
				"getLockTimeoutMicros");
		try {
			return (Long) method.invoke(env);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}
}