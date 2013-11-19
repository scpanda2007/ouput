package viso.framework.profile;

/**
 * A profile operation which aggregates a count of how many times the operation
 * occurred until it is explicitly cleared.
 */
public interface AggregateProfileOperation extends ProfileOperation {

    /**
     * Gets aggregate number of times this operation has been reported.
     * 
     * @return the current count of operation reports
     */
    long getCount();
    
    /**
     * Clear the count of operation reports.
     */
    void clearCount();
}
