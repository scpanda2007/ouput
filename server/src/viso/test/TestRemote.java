package viso.test;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface TestRemote extends Remote{
	public void testRemote(String anyArg) throws IOException;
}

class DummyRemoteCall extends UnicastRemoteObject implements TestRemote{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DummyRemoteCall() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int counter = 0;
	
	public static Object obj = new Object();
	
	@Override
	public void testRemote(String anyArg) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello , this is a test of DummyRemoteCall.--"+anyArg+"--"+System.currentTimeMillis());
		counter++;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(counter>10){
			synchronized(obj){
				obj.notifyAll();
			}
		}
	}
}

