package viso.framework.kernel;


/**
 * This interface provides a handle to a recurring task in the scheduler.
 */
public interface RecurringTaskHandle
{

    /**
     * Cancels the associated recurring task. A recurring task may be
     * cancelled before it is started.
     *
     * @throws IllegalStateException if the task has already been cancelled
     */
    void cancel();

    /**
     * Starts the associated recurring task. A recurring task will not start
     * running until this method is called.
     *
     * @throws IllegalStateException if the task has already been started,
     *                               or has been cancelled
     */
    void start();

}