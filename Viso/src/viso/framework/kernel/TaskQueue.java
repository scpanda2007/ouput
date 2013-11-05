package viso.framework.kernel;

import viso.framework.auth.Identity;

/**
 * This interface defines a dependency between tasks, such that tasks are
 * run in the order in which they are submitted, and the next task isn't
 * started until the current task has completed.
 */
public interface TaskQueue {

    /**
     * Adds a task to this dependency queue.
     *
     * @param task the {@code KernelRunnable} to add
     * @param owner the {@code Identity} that owns the task
     */
    void addTask(KernelRunnable task, Identity owner);
}

