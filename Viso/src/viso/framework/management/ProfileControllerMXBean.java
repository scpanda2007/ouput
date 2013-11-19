package viso.framework.management;
import viso.framework.profile.ProfileCollector.ProfileLevel;

/**
 * The management information for this node's profiling data.  The profiling
 * levels for each of the individual profile consumers can be modified through 
 * this interface.
 * <p>
 * An instance implementing this MBean can be obtained from the from the 
 * {@link java.lang.management.ManagementFactory.html#getPlatformMBeanServer() 
 * getPlatformMBeanServer} method.
 * <p>
 * The {@code ObjectName} for uniquely identifying this MBean is
 * {@value #MXBEAN_NAME}.
 * 
 */
public interface ProfileControllerMXBean {
    /** The name for uniquely identifying this MBean. */
    String MXBEAN_NAME = "viso.framework:type=ProfileController";
    
    // Maybe add a way to add/remove listeners?
    
    /**
     * Gets the default profile level for all newly created consumers.
     * 
     * @return the default profile level for consumer creation
     */
    ProfileLevel getDefaultProfileLevel();
    
    /**
     * Sets the default profile level for all newly created consumers.
     * 
     * @param level the default profile level for consumer creation
     */
    void setDefaultProfileLevel(ProfileLevel level);
    
    /**
     * Gets the names of all profile consumers in the system.
     * 
     * @return the names of all the profile consumers
     */
    String[] getProfileConsumers();
    
    /**
     * Gets the current profile level of the named consumer.
     * 
     * @param consumer the consumer name
     *
     * @return the profile level for the named consumer
     * @throws IllegalArgumentException if the consumer has not been created
     */
    ProfileLevel getConsumerLevel(String consumer);
    
    /**
     * Sets the current profile level of the named consumer.
     * 
     * @param consumer the consumer name
     * @param level the profile level
     * 
     * @throws IllegalArgumentException if the consumer has not been created
     */
    void setConsumerLevel(String consumer, ProfileLevel level);
}
