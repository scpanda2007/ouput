package viso.sbeans.framework.transaction;

public class VTransaction {
	Thread thread;
	
	private VTransaction(Thread thread){
		this.thread = thread;
	}
	
	public static VTransaction createTransaction(){
		return new VTransaction(Thread.currentThread());
	}
	
	public void checkThread(){
		if(Thread.currentThread()==thread) return;
		throw new IllegalStateException("Error , not in the right thread.");
	}
}
