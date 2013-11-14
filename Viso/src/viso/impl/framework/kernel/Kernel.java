package viso.impl.framework.kernel;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.framework.service.TransactionProxy;
import viso.impl.framework.auth.IdentityImpl;
import viso.impl.framework.profile.ProfileCollectorHandleImpl;
import viso.impl.framework.profile.ProfileCollectorImpl;
import viso.impl.framework.service.transaction.TransactionCoordinator;
import viso.impl.framework.service.transaction.TransactionCoordinatorImpl;
import viso.test.framework.kernel.DummyAccessCoordinatorHandle;
import viso.util.tools.LoggerWrapper;
import viso.util.tools.PropertiesWrapper;
import viso.framework.auth.Identity;
import viso.framework.kernel.NodeType;
import viso.framework.profile.ProfileCollector.ProfileLevel;

class Kernel {
	// logger for this class
	private static final LoggerWrapper logger = new LoggerWrapper(Logger
			.getLogger(Kernel.class.getName()));

	//////////////////////////////////////////////////////////////////////////////////
	// the property for setting profiling levels
	public static final String PROFILE_LEVEL_PROPERTY = "viso.impl.kernel.profile.level";

	//////////////////////////////////////////////////////////////////////////////////

	// default timeout the kernel's shutdown method (15 minutes)
	private static final int DEFAULT_SHUTDOWN_TIMEOUT = 15 * 60000;

	// the proxy used by all transactional components
	private static final TransactionProxy proxy = new TransactionProxyImpl();

	// the properties used to start the application
	private final PropertiesWrapper wrappedProperties;

	// collector of profile information, and an associated handle
	private final ProfileCollectorImpl profileCollector;
	private final ProfileCollectorHandleImpl profileCollectorHandle;

	// the schedulers used for transactional and non-transactional tasks
	private final TransactionSchedulerImpl transactionScheduler;
	private final TaskSchedulerImpl taskScheduler;

	// The system registry which contains all shared system components
	private final ComponentRegistryImpl systemRegistry;
	
	// shutdown controller that can be passed to components who need to be able 
    // to issue a kernel shutdown. the watchdog also constains a reference for
    // services to call shutdown.
    private final KernelShutdownControllerImpl shutdownCtrl = 
            new KernelShutdownControllerImpl();

	// the application that is running in this kernel
	private KernelContext application;

	///////////////////////////////////////////////////////////////////////////
	// specifies whether this node has already been shutdown
	private boolean isShutdown = false;

	protected Kernel(Properties appProperties) throws Exception {

		wrappedProperties = new PropertiesWrapper(appProperties);

		try {
			// See if we're doing any profiling.
			String level = wrappedProperties.getProperty(
					PROFILE_LEVEL_PROPERTY, ProfileLevel.MIN.name());
			ProfileLevel profileLevel;
			try {
				profileLevel = ProfileLevel.valueOf(level.toUpperCase());
				if (logger.isLoggable(Level.CONFIG)) {
					logger.log(Level.CONFIG, "Profiling level is {0}", level);
				}
			} catch (IllegalArgumentException iae) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, "Unknown profile level {0}",
							level);
				}
				throw iae;
			}

			// Create the system registry
			systemRegistry = new ComponentRegistryImpl();

			profileCollector = new ProfileCollectorImpl(profileLevel,
					appProperties, systemRegistry);
			profileCollectorHandle = new ProfileCollectorHandleImpl(
					profileCollector);

			// create the access coordinator
			AccessCoordinatorHandle accessCoordinator = new DummyAccessCoordinatorHandle();

			TransactionCoordinator transactionCoordinator = new TransactionCoordinatorImpl(
					appProperties, profileCollectorHandle);

			// create the schedulers, and provide an empty context in case
			// any profiling components try to do transactional work
			transactionScheduler = new TransactionSchedulerImpl(appProperties,
					transactionCoordinator, profileCollectorHandle,
					accessCoordinator);
			taskScheduler = new TaskSchedulerImpl(appProperties,
					profileCollectorHandle);

			KernelContext ctx = new StartupKernelContext("Kernel");
			transactionScheduler.setContext(ctx);
			taskScheduler.setContext(ctx);

			systemRegistry.addComponent(accessCoordinator);
			systemRegistry.addComponent(transactionScheduler);
			systemRegistry.addComponent(taskScheduler);
			systemRegistry.addComponent(profileCollector);
			
			if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "The Kernel is ready, version: {0}",
                        "1");
            }

            // the core system is ready, so start up the application
            createAndStartApplication();
			
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.logThrow(Level.SEVERE, e, "Failed on Kernel boot");
			}
			// shut down whatever we've started
			shutdown();
			throw e;
		}

	}
	
    /**
     * Helper that starts an application. This method 
     * configures the <code>Service</code>s associated with the
     * application and then starts the application.
     * 
     * @throws Exception if there is any error in startup
     */
    private void createAndStartApplication() throws Exception {
        String appName = wrappedProperties.getProperty(
                StandardProperties.APP_NAME);

        if (logger.isLoggable(Level.CONFIG)) {
            logger.log(Level.CONFIG, "{0}: starting application", appName);
        }

        // start the service creation 
        IdentityImpl owner = new SystemIdentity("app:" + appName);
        createServices(appName, owner);
        startApplication(appName, owner);
    }


    /**
     * Creates each of the <code>Service</code>s and their corresponding
     * <code>Manager</code>s (if any) in order, in preparation for starting
     * up an application.
     */
    private void createServices(String appName, Identity owner) 
        throws Exception
    {
        if (logger.isLoggable(Level.CONFIG)) {
            logger.log(Level.CONFIG, "{0}: starting services", appName);
        }

        // create and install a temporary context to use during startup
        application = new StartupKernelContext(appName);
        transactionScheduler.setContext(application);
        taskScheduler.setContext(application);
        ContextResolver.setTaskState(application, owner);

        // tell the AppContext how to find the managers
//        InternalContext.setManagerLocator(new ManagerLocatorImpl());
//        
//        try {
//            fetchServices((StartupKernelContext) application);
//        } catch (Exception e) {
//            if (logger.isLoggable(Level.SEVERE)) {
//                logger.logThrow(Level.SEVERE, e, "{0}: failed to create " +
//                                "services", appName);
//            }
//            throw e;
//        }

        // with the managers fully created, swap in a permanent context
        application = new KernelContext(application);
        transactionScheduler.setContext(application);
        taskScheduler.setContext(application);
        ContextResolver.setTaskState(application, owner);
        
        // notify all of the services that the application state is ready
        try {
            application.notifyReady();
        } catch (Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.logThrow(Level.SEVERE, e, "{0}: failed when notifying " +
                                "services that application is ready", appName);
            }
            throw e;
        }
        
        // enable the shutdown controller once the components and services
        // are setup to allow a node shutdown call from either of them.
        shutdownCtrl.setReady();
    }

    /** Start the application, throwing an exception if there is a problem. */
    private void startApplication(String appName, Identity owner) 
        throws Exception
    {
        // at this point the services are ready, so the final step
        // is to initialize the application by running a special
        // KernelRunnable in an unbounded transaction, unless we're
        // running without an application
        NodeType type = 
            NodeType.valueOf(
                wrappedProperties.getProperty(StandardProperties.NODE_TYPE));
        if (!type.equals(NodeType.coreServerNode)) {
            try {
                if (logger.isLoggable(Level.CONFIG)) {
                    logger.log(Level.CONFIG, "{0}: starting application",
                               appName);
                }

//                transactionScheduler.
//                    runUnboundedTask(
//                        new AppStartupRunner(wrappedProperties.getProperties()),
//                        owner);

                logger.log(Level.INFO, 
                           "{0}: application is ready", application);
            } catch (Exception e) {
                if (logger.isLoggable(Level.CONFIG)) {
                    logger.logThrow(Level.CONFIG, e, "{0}: failed to " +
                                    "start application", appName);
                }
                throw e;
            }
        } else {
            // we're running without an application, so we're finished
            logger.log(Level.INFO, "{0}: non-application context is ready",
                       application);
        }
    }

	/**
	 * This is an object created by the {@code Kernel} and passed to the 
	 * services and components which are given shutdown privileges. This object 
	 * allows the {@code Kernel} to be referenced when a shutdown of the node is
	 * necessary, such as when a service on the node has failed or has become
	 * inconsistent. This class can only be instantiated by the {@code Kernel}.
	 */
	private final class KernelShutdownControllerImpl implements
			KernelShutdownController {
		private volatile long nodeId = -1;
		//        private WatchdogService watchdogSvc = null;
		private boolean shutdownQueued = false;
		private boolean isReady = false;
		private final Object shutdownQueueLock = new Object();

		/** Provides the shutdown controller with the local node id. */
		public void setNodeId(long id) {
			nodeId = id;
		}

		/**
		 * This method gives the shutdown controller a handle to the
		 * {@code WatchdogService}. Components will use this handle to report a
		 * failure to the watchdog service instead of shutting down directly.
		 * This which ensures that the server is properly notified when a node
		 * needs to be shut down. This handle can only be set once, any call
		 * after that will be ignored.
		 */
		//        public void setWatchdogHandle(WatchdogService watchdogSvc) {
		//            if (this.watchdogSvc != null) {
		//                return; // do not allow overwriting the watchdog once it's set
		//            }
		//            this.watchdogSvc = watchdogSvc;
		//        }
		/**
		 * This method flags the controller as being ready to issue shutdowns.
		 * If a shutdown was previously queued, then shutdown the node now.
		 */
		public void setReady() {
			synchronized (shutdownQueueLock) {
				isReady = true;
				if (shutdownQueued) {
					shutdownNode(this);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void shutdownNode(Object caller) {
			synchronized (shutdownQueueLock) {
				if (isReady) {
					// service shutdown; we have already gone through notifying
					// the server, so shutdown the node right now
					//                    if (caller instanceof WatchdogService) {
					//                        runShutdown();
					//                    } else {
					//                        // component shutdown; we go through the watchdog to
					//                        // cleanup and notify the server first
					//                        if (nodeId != -1 && watchdogSvc != null) {
					//                            watchdogSvc.
					//                                reportFailure(nodeId,
					//                                              caller.getClass().toString());
					//                        } else {
					//                            // shutdown directly if data service and watchdog
					//                            // have not been setup
					//                            runShutdown();
					//                        }
					//                    }
					runShutdown();//TODO:just test
				} else {
					// queue the request if the Kernel is not ready
					shutdownQueued = true;
				}
			}
		}

		/**
		 * Shutdown the node. This is run in a different thread to prevent a 
		 * possible deadlock due to a service or component's doShutdown()
		 * method waiting for the thread it was issued from to shutdown.
		 * For example, the watchdog service's shutdown method would block if
		 * a Kernel shutdown was called from RenewThread.
		 */
		private void runShutdown() {
			logger.log(Level.WARNING, "Controller issued node shutdown.");

			new Thread(new Runnable() {
				public void run() {
					shutdown();
				}
			}).start();
		}
	}

	/**
	 * Timer that will call {@link System#exit System.exit} after a timeout
	 * period to force the process to quit if the node shutdown process takes
	 * too long. The timer is started as a daemon so the task won't be run if
	 * a shutdown completes successfully.
	 */
	private void startShutdownTimeout(final int timeout) {
		new Timer(true).schedule(new TimerTask() {
			public void run() {
				System.exit(1);
			}
		}, timeout);
	}

	/**
	 * Shut down all services (in reverse order) and the schedulers.
	 */
	synchronized void shutdown() {
		if (isShutdown) {
			return;
		}
		startShutdownTimeout(DEFAULT_SHUTDOWN_TIMEOUT);

		logger.log(Level.FINE, "Kernel.shutdown() called.");
		if (application != null) {
			application.shutdownServices();
		}
		if (profileCollector != null) {
			profileCollector.shutdown();
		}
		// The schedulers must be shut down last.
		if (transactionScheduler != null) {
			transactionScheduler.shutdown();
		}
		if (taskScheduler != null) {
			taskScheduler.shutdown();
		}

		logger.log(Level.FINE, "Node is shut down.");
		isShutdown = true;
	}

}
