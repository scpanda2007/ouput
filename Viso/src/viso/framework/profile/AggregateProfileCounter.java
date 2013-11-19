package viso.framework.profile;

/**
 * A profile counter which is initially {@code 0}, and increments until it
 * is explicitly cleared.
 */
public interface AggregateProfileCounter extends ProfileCounter {

    /**
     * Gets the current counter value.
     * 
     * @return the current count value
     */
    long getCount();
    
    /**
     * Clear the counter, resetting it to {@code 0}.
     */
    void clearCount();
}
