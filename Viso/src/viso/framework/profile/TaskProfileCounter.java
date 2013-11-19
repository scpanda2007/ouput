package viso.framework.profile;


/**
 * A counter which provides task-local information to {@link ProfileReport}s.
 * <p>
 * If the counter is modified during a given task, the {@code ProfileReport}
 * for that task will include the modification, and exclude changes made while
 * running other tasks.
 */
public interface TaskProfileCounter extends ProfileCounter {

    /** 
     * {@inheritDoc}
     * 
     * @throws IllegalStateException if this is called outside the scope
     *                               of a task run through the scheduler
     */
    void incrementCount();

    /** 
     * {@inheritDoc}
     * 
     * @throws IllegalStateException if this is called outside the scope
     *                               of a task run through the scheduler
     */
    void incrementCount(long value);
}
