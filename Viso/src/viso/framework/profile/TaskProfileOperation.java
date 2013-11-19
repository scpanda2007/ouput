package viso.framework.profile;

/**
 * An operation which provides task-local information to {@link ProfileReport}s.
 * <p>
 * If operation occurs during a given task, the {@code ProfileReport}
 * for that task will include the operation.
 */
public interface TaskProfileOperation extends ProfileOperation {

    /**
     * {@inheritDoc} 
     * 
     * @throws IllegalStateException if this is called outside the scope
     *                               of a task run through the scheduler
     */
    void report();
}
