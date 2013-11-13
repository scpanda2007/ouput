package viso.impl.framework.kernel;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import viso.framework.service.TransactionProxy;
import viso.impl.framework.profile.simple.ProfileCollectorHandleImpl;
import viso.impl.framework.profile.simple.ProfileCollectorImpl;
import viso.util.tools.LoggerWrapper;
import viso.util.tools.PropertiesWrapper;
import viso.framework.profile.ProfileCollector.ProfileLevel;

class Kernel {
    // logger for this class
    private static final LoggerWrapper logger =
        new LoggerWrapper(Logger.getLogger(Kernel.class.getName()));
    
    //////////////////////////////////////////////////////////////////////////////////
    // the property for setting profiling levels
    public static final String PROFILE_LEVEL_PROPERTY =
        "viso.impl.kernel.profile.level";
    
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
    private final TransactionSchedulerImpl transactionScheduler = null;
    private final TaskSchedulerImpl taskScheduler;
    
    // The system registry which contains all shared system components
    private final ComponentRegistryImpl systemRegistry;
    
    protected Kernel(Properties appProperties) throws Exception {
    	
    	wrappedProperties = new PropertiesWrapper(appProperties);
    	
    	// See if we're doing any profiling.
        String level = wrappedProperties.getProperty(PROFILE_LEVEL_PROPERTY,
                ProfileLevel.MIN.name());
        ProfileLevel profileLevel;
        try {
            profileLevel = 
                    ProfileLevel.valueOf(level.toUpperCase());
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
                appProperties,
                systemRegistry);
        profileCollectorHandle = 
        		new ProfileCollectorHandleImpl(profileCollector);
        
        taskScheduler =
            new TaskSchedulerImpl(appProperties, profileCollectorHandle);
        
    }
}
