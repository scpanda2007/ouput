package viso.framework.profile;

/**
 * A profile sample which provides information to {@link ProfileReport}s.
 * <p>
 * If data is added to the sample during a given task, the {@code ProfileReport}
 * for that task will include the changes made, and exclude changes made while
 * running other tasks.
 */
public interface TaskProfileSample extends ProfileSample {

    /** 
     * {@inheritDoc}
     * 
     * @throws IllegalStateException if this is called outside the scope
     *                               of a task run through the scheduler
     */
    void addSample(long value);
}