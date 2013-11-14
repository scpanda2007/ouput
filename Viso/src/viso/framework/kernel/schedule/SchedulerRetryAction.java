package viso.framework.kernel.schedule;


/**
 * Enumeration of possible retry actions that a scheduler can use to
 * retry a failed task.
 */
public enum SchedulerRetryAction {

    /**
     * Indicates that a task should be dropped.
     */
    DROP,

    /**
     * Indicates that a task should be retried at some point in the future.
     */
    RETRY_LATER,

    /**
     * Indicates that a task should be retried immediately.
     */
    RETRY_NOW;

}

