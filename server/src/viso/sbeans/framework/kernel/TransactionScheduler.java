package viso.sbeans.framework.kernel;

import viso.sbeans.framework.transaction.VTransaction;

class ScheduleTask{
	private final Runnable runnable;
	public ScheduleTask(Runnable runnable){
		this.runnable = runnable;
	}
	public void run(){
		runnable.run();
	}
}

public class TransactionScheduler {
	
	/**
	 * ��һ���߳�����
	 * */
	public void scheduleTask(Runnable runnable){
		
	}
	
	/**
	 * ֱ����ԭ�߳�������
	 * */
	public void runTask(Runnable runnable){
		executeInner(new ScheduleTask(runnable));
	}
	
	private void executeInner(ScheduleTask task) {
		VTransaction transaction = ThreadContext.getTransaction();
		try {
			if(transaction==null)
				transaction = VTransaction.createTransaction();
			ThreadContext.setTransaction(transaction);
			task.run();
		} finally {
			ThreadContext.clearTransaction(transaction);
		}
	}
}
