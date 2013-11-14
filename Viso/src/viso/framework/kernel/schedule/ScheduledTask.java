package viso.framework.kernel.schedule;

import viso.framework.auth.Identity;
import viso.framework.kernel.KernelRunnable;
import viso.framework.kernel.Priority;
import viso.framework.kernel.RecurringTaskHandle;

/**
 * This interface represents a single task that has been accepted by a
 * scheduler. It is used by implementations of {@code SchedulerQueue}
 * to manage the tasks in the queue.
 */
public interface ScheduledTask {

    /** Identifier that represents a non-recurring task. */
    int NON_RECURRING = -1;

    /** Identifier that represents an unbounded timeout. */
    int UNBOUNDED = -1;

    /**
     * Returns the task.
     *
     * @return the {@code KernelRunnable} to run
     */
    KernelRunnable getTask();

    /**
     * Returns the owner.
     *
     * @return the {@code Identity} that owns the task
     */
    Identity getOwner();

    /**
     * Returns the priority.
     *
     * @return the {@code Priority}
     */
    Priority getPriority();

    /**
     * Returns the time at which this task is scheduled to start.
     *
     * @return the scheduled run time for the task
     */
    long getStartTime();

    /**
     * Returns the period for the task if it's recurring, or
     * {@code NON_RECURRING} if this is not a recurring task.
     *
     * @return the period between recurring executions.
     */
    long getPeriod();

    /**
     * Returns the try count (the number of times that this task has been
     * attempted).
     *
     * @return the total number of times this task has been retried
     */
    int getTryCount();

    /**
     * Returns the transaction timeout.  A Scheduler may or may not choose
     * to use this timeout value.
     *
     * @return the transaction timeout to use for this task.
     */
    long getTimeout();

    /**
     * Returns the {@code Throwable} that was the last failure of this task
     * or {@code null} if it has never been run or never failed.
     *
     * @return the {@code Throwable} that was the cause of the last failure
     */
    Throwable getLastFailure();

    /**
     * Sets the priority for this task.
     *
     * @param priority the new priority for this task
     */
    void setPriority(Priority priority);

    /**
     * Sets the transaction timeout for this task.
     *
     * @param timeout the new transaction timeout for this task
     */
    void setTimeout(long timeout);

    /**
     * Returns whether this is a recurring task. If this is not a recurring
     * task then {@code getPeriod} should always return {@code NON_RECURRING}.
     *
     * @return {@code true} if this task is a recurring task,
     *         {@code false} otherwise.
     */
    boolean isRecurring();

    /**
     * Returns the {@code RecurringTaskHandle} associated with this task if
     * this task is recurring, or {@code null} if this is not recurring.
     *
     * @return the associated {@code RecurringTaskHandle} or {@code null}
     */
    RecurringTaskHandle getRecurringTaskHandle();

    /**
     * Returns whether this task has been cancelled.
     *
     * @return {@code true} if this {@code ScheduledTask} has been cancelled,
     *         {@code false} otherwise
     */
    boolean isCancelled();

    /**
     * Cancel this {@code ScheduledTask}. Note that if the task is already
     * running then calling this method may not have any affect.  Also note
     * that this method should never be called with a value of {@code true}
     * for the {@code block} parameter if the calling thread is also currently
     * running this {@code ScheduledTask}.
     *
     * @param block if {@code true}, this call will block until the task is
     *              cancelled or has completed, if {@code false}, it will not
     *              block
     *
     * @return {@code true} if the task was cancelled by this call,
     *         {@code false} otherwise
     */
    boolean cancel(boolean block);

}