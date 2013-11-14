package viso.test.framework.kernel;

import java.util.concurrent.atomic.AtomicInteger;
import viso.framework.kernel.KernelRunnable;

public class TestTransactionScheduleImpl {
    public static class DependentTask implements KernelRunnable {
        private static final Object lock = new Object();
        private static boolean isRunning = false;
        private static int objNumberSequence = 0;
        private static volatile int nextExpectedObjNumber = 0;
        private final int objNumber;
        private final AtomicInteger runCounter;
        public DependentTask(AtomicInteger runCounter) {
            synchronized (lock) {
                objNumber = objNumberSequence++;
            }
            this.runCounter = runCounter;
        }
        public String getBaseTaskType() {
            return DependentTask.class.getName();
        }
        public void run() throws Exception {
            synchronized (lock) {
                if (isRunning)
                    throw new RuntimeException("another task was running");
                isRunning = true;
            }
            if (nextExpectedObjNumber != objNumber)
                throw new RuntimeException("tasks ran out-of-order");
            nextExpectedObjNumber++;
            runCounter.incrementAndGet();
            isRunning = false;
        }
    }
}
