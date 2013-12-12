package viso.sbeans.framework.kernel;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.util.TaskQueue;

public class TaskScheduler {
	
	private ExecutorService executor;
	
	private int requestThreadsNum = 4;
	
	public boolean isShutDown = false;
	
	public TaskScheduler(){
		executor = Executors.newFixedThreadPool(requestThreadsNum, new NamedThreadFactory("TaskScheduler"));
	}
	
	public void shutdown() {
		synchronized (this) {
			if (isShutDown) {
				return;
			}
			executor.shutdown();
		}
	}
	
	public TaskQueue createTaskQueue(){
		synchronized(this){
			if(isShutDown){
				throw new IllegalStateException("The TaskScheduler is already shutdown.");
			}
			return new TaskQueueImpl();
		}
	}
	
	public void sumbit(Runnable runnable){
		executor.submit(new TaskInQueue(runnable,null));
	}
	
	private class TaskQueueImpl implements TaskQueue{
		
		private final LinkedList<TaskInQueue> tasks = new LinkedList<TaskInQueue>();
		
		private boolean isRunning = false;
		
		@Override
		public void submit(Runnable task) {
			synchronized (this) {
				// TODO Auto-generated method stub
				if (!isRunning) {
					executor.submit(new TaskInQueue(task, this));
					isRunning = true;
					return;
				}
				tasks.offer(new TaskInQueue(task, this));
			}
		}
		
		public void scheduleNext() {
			synchronized (this) {
				if (tasks.isEmpty()){
					isRunning = false;
					return;
				}
				TaskInQueue task = tasks.poll();
				executor.submit(task);
			}
		}
		
	}
	
	private class TaskInQueue implements Runnable{
		
		private TaskQueueImpl queue;
		private final Runnable runable;
		public TaskInQueue(Runnable task,TaskQueueImpl queue) {
			// TODO Auto-generated constructor stub
			runable = task;
			this.queue = queue;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				runable.run();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(queue!=null)queue.scheduleNext();//TODO: should i finnally call this line ? 
			}
		}
	}
}
