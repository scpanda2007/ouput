package viso.framework.profile;

/**
 * An operation which has occurred.
 * <p>
 * Profile operations are created with calls to {@link 
 * ProfileConsumer#createOperation ProfileConsumer.createOperation}.  An 
 * operations's name includes both the {@code name} supplied to 
 * {@code createOperation} and the value of {@link ProfileConsumer#getName}.
 */
public interface ProfileOperation {

    /**
     * Returns the name of this operation.
     *
     * @return the name
     */
    String getName();

    /**
     * Tells this operation to report that it is happening. 
     */
    void report();
}

