package viso.sbeans.framework.util;

/**有序的任务队列*/
public interface TaskQueue {
	public void submit(Runnable task);
}
