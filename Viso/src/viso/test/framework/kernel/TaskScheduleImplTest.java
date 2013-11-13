package viso.test.framework.kernel;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import viso.framework.auth.Identity;
import viso.framework.kernel.KernelRunnable;
import viso.framework.kernel.RecurringTaskHandle;
import viso.framework.kernel.TaskQueue;
import viso.framework.kernel.TaskReservation;
import viso.framework.kernel.TaskScheduler;
import viso.test.framework.util.DummyIdentity;
import viso.test.framework.util.TestAbstractKernelRunnable;
import viso.test.framework.util.VisoTestNode;
import viso.test.framework.kernel.TestTransactionScheduleImpl.DependentTask;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class TaskScheduleImplTest {

	// an empty task that does nothing
    private static final KernelRunnable testTask =
        new TestAbstractKernelRunnable() {
            public void run() throws Exception {}
        };
        
    private TaskScheduler taskScheduler;
    private Identity taskOwner;
    private volatile int taskCount; 
    private VisoTestNode node = null;
    
    public TaskScheduleImplTest(){}
    
    @Before
    public void setUp() throws Exception {
//		super.setUp();
		taskCount = 0;
		taskOwner = new DummyIdentity();
		node = new VisoTestNode("TestTaskScheduleImpl");
		taskScheduler = node.getInstance(TaskScheduler.class);
	}

    @After
	public void tearDown() throws Exception {
//		super.tearDown();
    	node.shutdown();
	}

    /**
     * Task reservation tests.
     */

    @Test public void reserveTask() throws Exception {
        taskScheduler.reserveTask(testTask, taskOwner);
    }

    @Test public void reserveTaskDelayed() throws Exception {
        taskScheduler.reserveTask(testTask, taskOwner,
				  System.currentTimeMillis() + 100);
    }

    @Test public void reserveTasks() throws Exception {
        taskScheduler.reserveTask(testTask, taskOwner);
        taskScheduler.reserveTask(testTask, taskOwner);
        taskScheduler.reserveTask(testTask, taskOwner);
        taskScheduler.reserveTask(testTask, taskOwner);
        taskScheduler.reserveTask(testTask, taskOwner);
        taskScheduler.reserveTask(testTask, taskOwner);
    }

    @Test public void reserveTasksDelayed() throws Exception {
        long time = System.currentTimeMillis() + 100;
        taskScheduler.reserveTask(testTask, taskOwner, time);
        taskScheduler.reserveTask(testTask, taskOwner, time);
        taskScheduler.reserveTask(testTask, taskOwner, time);
        taskScheduler.reserveTask(testTask, taskOwner, time);
        taskScheduler.reserveTask(testTask, taskOwner, time);
        taskScheduler.reserveTask(testTask, taskOwner, time);
    }

    @Test (expected=NullPointerException.class)
        public void reserveTaskNull() throws Exception {
        taskScheduler.reserveTask(null, taskOwner);
    }

    @Test (expected=NullPointerException.class)
        public void reserveTaskNullOwner() throws Exception {
        taskScheduler.reserveTask(testTask, null);
    }

    @Test (expected=NullPointerException.class)
        public void reserveTaskDelayedNull() throws Exception {
        taskScheduler.reserveTask(null, taskOwner, System.currentTimeMillis());
    }

    @Test (expected=NullPointerException.class)
        public void reserveTaskDelayedNullOwner() throws Exception {
        taskScheduler.reserveTask(testTask, null, System.currentTimeMillis());
    }

    @Test public void reserveTaskDelayedTimepassed() throws Exception {
        taskScheduler.reserveTask(testTask, taskOwner,
                                  System.currentTimeMillis() - 50);
    }

    @Test public void useReservedTask() throws Exception {
        TaskReservation reservation = 
            taskScheduler.reserveTask(new IncrementRunner(), taskOwner);
        reservation.use();
        Thread.sleep(200L);
        assertEquals(1, taskCount);
    }

    @Test public void useReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(new IncrementRunner(), taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.use();
        Thread.sleep(300L);
        assertEquals(1, taskCount);
    }

    @Test public void cancelReservedTask() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(new IncrementRunner(), taskOwner);
        reservation.cancel();
        Thread.sleep(200L);
        assertEquals(0, taskCount);
    }

    @Test public void cancelReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(new IncrementRunner(), taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.cancel();
        Thread.sleep(300L);
        assertEquals(0, taskCount);
    }

    @Test (expected=IllegalStateException.class)
        public void reuseReservedTask() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner);
        reservation.use();
        reservation.use();
    }

    @Test (expected=IllegalStateException.class)
        public void reuseReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.use();
        reservation.use();
    }

    @Test (expected=IllegalStateException.class)
        public void recancelReservedTask() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner);
        reservation.cancel();
        reservation.cancel();
    }

    @Test (expected=IllegalStateException.class)
        public void recancelReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.cancel();
        reservation.cancel();
    }

    @Test (expected=IllegalStateException.class)
        public void cancelAfterUseReservedTask() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner);
        reservation.use();
        reservation.cancel();
    }

    @Test (expected=IllegalStateException.class)
        public void cancelAfterUseReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.use();
        reservation.cancel();
    }

    @Test (expected=IllegalStateException.class)
        public void useAfterCancelReservedTask() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner);
        reservation.cancel();
        reservation.use();
    }

    @Test (expected=IllegalStateException.class)
        public void useAfterCancelReservedTaskDelayed() throws Exception {
        TaskReservation reservation =
            taskScheduler.reserveTask(testTask, taskOwner,
                                      System.currentTimeMillis() + 50);
        reservation.cancel();
        reservation.use();
    }

    /**
     * Task scheduling tests.
     */

    @Test public void scheduleTask() throws Exception {
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        Thread.sleep(200L);
        assertEquals(1, taskCount);
    }

    @Test public void scheduleTaskDelayed() throws Exception {
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner,
                                   System.currentTimeMillis() + 50);
        Thread.sleep(300L);
        assertEquals(1, taskCount);
    }

    @Test public void scheduleTasks() throws Exception {
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner);
        Thread.sleep(400L);
        assertEquals(6, taskCount);
    }

    @Test public void scheduleTasksDelayed() throws Exception {
        long time = System.currentTimeMillis() + 50;
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        taskScheduler.scheduleTask(new IncrementRunner(), taskOwner, time);
        Thread.sleep(1000L);
        assertEquals(6, taskCount);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskNull() throws Exception {
        taskScheduler.scheduleTask(null, taskOwner);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskOwnerNull() throws Exception {
        taskScheduler.scheduleTask(testTask, null);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskDelayedNull() throws Exception {
        taskScheduler.scheduleTask(null, taskOwner, System.currentTimeMillis());
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskDelayedOwnerNull() throws Exception {
        taskScheduler.scheduleTask(testTask, null, System.currentTimeMillis());
    }

    /**
     * Recurring task scheduling tests.
     */

    @Test public void scheduleTaskRecurring() throws Exception {
        taskScheduler.scheduleRecurringTask(testTask, taskOwner,
                                            System.currentTimeMillis(), 50);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskRecurringNull() throws Exception {
        taskScheduler.scheduleRecurringTask(null, taskOwner,
                                            System.currentTimeMillis(), 50);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleTaskRecurringNullOwner() throws Exception {
        taskScheduler.scheduleRecurringTask(testTask, null,
                                            System.currentTimeMillis(), 50);
    }

    @Test (expected=IllegalArgumentException.class)
        public void scheduleTaskRecurringIllegalPeriod() throws Exception {
        taskScheduler.scheduleRecurringTask(testTask, taskOwner,
                                            System.currentTimeMillis(), -1);
    }

    @Test public void cancelAfterStartRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(new IncrementRunner(),
                                                taskOwner,
                                                System.currentTimeMillis() + 50,
                                                50);
        handle.start();
        handle.cancel();
        Thread.sleep(200L);
        assertEquals(0, taskCount);
    }

    @Test public void startSleepAndCancelRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(new IncrementRunner(),
                                                taskOwner,
                                                System.currentTimeMillis(),
                                                200);
        handle.start();
        Thread.sleep(300L);
        assertEquals(2, taskCount);
        handle.cancel();
        Thread.sleep(200L);
        assertEquals(2, taskCount);
    }

    @Test public void cancelRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(new IncrementRunner(),
                                                taskOwner,
                                                System.currentTimeMillis(),
                                                50);
        handle.cancel();
        Thread.sleep(100L);
        assertEquals(0, taskCount);
    }

    @Test (expected=IllegalStateException.class)
        public void restartRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(testTask, taskOwner,
                                                System.currentTimeMillis(),
                                                100);
        handle.start();
        try {
            handle.start();
        } finally {
            handle.cancel();
        }
    }

    @Test (expected=IllegalStateException.class)
        public void recancelRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(testTask, taskOwner,
                                                System.currentTimeMillis(),
                                                100);
        handle.cancel();
        handle.cancel();
    }

    @Test (expected=IllegalStateException.class)
        public void startAfterCancelRecurringTask() throws Exception {
        RecurringTaskHandle handle =
            taskScheduler.scheduleRecurringTask(testTask, taskOwner,
                                                System.currentTimeMillis(),
                                                100);
        handle.cancel();
        handle.start();
    }

    /**
     * Test createTaskQueue.
     */

    @Test public void scheduleQueuedTasks() throws Exception {
        TaskQueue queue = taskScheduler.createTaskQueue();
        AtomicInteger runCount = new AtomicInteger(0);
        for (int i = 0; i < 100; i++)
            queue.addTask(new DependentTask(runCount), taskOwner);
        Thread.sleep(500L);
        assertEquals(100, runCount.get());
    }

    @Test (expected=NullPointerException.class)
        public void scheduleQueuedTasksNull() throws Exception {
        TaskQueue queue = taskScheduler.createTaskQueue();
        queue.addTask(null, taskOwner);
    }

    @Test (expected=NullPointerException.class)
        public void scheduleQueuedTasksOwnerNull() throws Exception {
        TaskQueue queue = taskScheduler.createTaskQueue();
        queue.addTask(new DependentTask(null), null);
    }

    /**
     * Utility classes.
     */

    private class IncrementRunner extends TestAbstractKernelRunnable {
        public void run() {
            taskCount++;
        }
    }
}
