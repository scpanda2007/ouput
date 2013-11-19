package viso.test.framework.service.store;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;

import org.junit.Assert;

import viso.framework.kernel.ComponentRegistry;
import viso.framework.kernel.NullAccessCoordinator;
import viso.framework.kernel.TaskScheduler;
import viso.framework.kernel.TransactionScheduler;
import viso.framework.service.TransactionProxy;
import viso.impl.framework.kernel.AccessCoordinatorHandle;
import viso.impl.framework.profile.ProfileCollectorHandle;
import viso.impl.framework.service.transaction.TransactionCoordinator;
import viso.test.framework.profile.DummyProfileCollectorHandle;
import viso.test.framework.util.DummyTransactionProxy;

/**
 * A basic environment for running a {@code DataStore} test.  This class
 * emulates the behavior of the {@code Kernel} class in setting up system
 * components.
 */
public class BasicDataStoreTestEnv extends Assert {

	/** The property for overriding the default access coordinator. */
	public final static String ACCESS_COORDINATOR_PROPERTY = "test.access.coordinator";

	/** The transaction proxy. */
	public final DummyTransactionProxy txnProxy = new DummyTransactionProxy();

	/** The profile collector handle. */
	public final DummyProfileCollectorHandle profileCollectorHandle = new DummyProfileCollectorHandle();

	/** The access coordinator. */
	public final AccessCoordinatorHandle accessCoordinator;

	/** The transaction scheduler. */
	public final TransactionScheduler txnScheduler;

	/** The task scheduler. */
	public final TaskScheduler taskScheduler;

	/**
	 * The system registry, which contains the access coordinator, transaction
	 * scheduler, and task scheduler.
	 */
	public final ComponentRegistry systemRegistry;

	/**
	 * Creates a basic environment for running a {@code DataStore} test, using
	 * a {@link NullAccessCoordinator} by default.
	 *
	 * @param	properties the configuration properties
	 */
	public BasicDataStoreTestEnv(Properties properties) {
		this(properties, NullAccessCoordinator.class.getName());
	}

	/**
	 * Creates a basic environment for running a {@code DataStore} test, using
	 * an access coordinator of the specified class by default.
	 *
	 * @param	properties the configuration properties
	 * @param	accessCoordinatorClassName the class name of the access
	 *		coordinator to use by default
	 */
	public BasicDataStoreTestEnv(Properties properties,
			String accessCoordinatorClassName) {
		try {
			/* Access coordinator */
			accessCoordinatorClassName = properties.getProperty(
					ACCESS_COORDINATOR_PROPERTY, accessCoordinatorClassName);
			Constructor<? extends AccessCoordinatorHandle> accessCoordCons = Class
					.forName(accessCoordinatorClassName).asSubclass(
							AccessCoordinatorHandle.class)
					.getDeclaredConstructor(Properties.class,
							TransactionProxy.class,
							ProfileCollectorHandle.class);
			accessCoordCons.setAccessible(true);
			accessCoordinator = accessCoordCons.newInstance(properties,
					txnProxy, profileCollectorHandle);
			/* Transaction scheduler */
			Constructor<? extends TransactionScheduler> txnSchedCons = Class
					.forName("viso.impl.framework.kernel.TransactionSchedulerImpl")
					.asSubclass(TransactionScheduler.class)
					.getDeclaredConstructor(Properties.class,
							TransactionCoordinator.class,
							ProfileCollectorHandle.class,
							AccessCoordinatorHandle.class);
			txnSchedCons.setAccessible(true);
			txnScheduler = txnSchedCons.newInstance(properties, txnProxy,
					profileCollectorHandle, accessCoordinator);
			/* Task scheduler */
			Constructor<? extends TaskScheduler> taskSchedCons = Class.forName(
					"viso.impl.framework.kernel.TaskSchedulerImpl").asSubclass(
					TaskScheduler.class).getDeclaredConstructor(
					Properties.class, ProfileCollectorHandle.class);
			taskSchedCons.setAccessible(true);
			taskScheduler = taskSchedCons.newInstance(properties,
					profileCollectorHandle);
			/* System registry */
			Class<? extends ComponentRegistry> sysRegClass = Class.forName(
					"viso.impl.framework.kernel.ComponentRegistryImpl")
					.asSubclass(ComponentRegistry.class);
			Constructor<? extends ComponentRegistry> sysRegCons = sysRegClass
					.getDeclaredConstructor();
			sysRegCons.setAccessible(true);
			systemRegistry = sysRegCons.newInstance();
			Method addComponentMethod = sysRegClass.getDeclaredMethod(
					"addComponent", Object.class);
			addComponentMethod.setAccessible(true);
			addComponentMethod.invoke(systemRegistry, accessCoordinator);
			addComponentMethod.invoke(systemRegistry, txnScheduler);
			addComponentMethod.invoke(systemRegistry, taskScheduler);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}
}