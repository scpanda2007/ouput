package viso.test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class DummyRemoteServer{
	public static void main(String args[]){
		try{
			DummyRemoteCall remote = new DummyRemoteCall();
			Registry registry = LocateRegistry.createRegistry(12345);
			registry.rebind("DummyRemoteTest",remote);
			synchronized(DummyRemoteCall.obj){
				try {
					DummyRemoteCall.obj.wait(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			registry.unbind("DummyRemoteTest");
			UnicastRemoteObject.unexportObject(registry, true);
			System.exit(0);
		}catch(RemoteException ex){
			ex.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("register done..");
	}
}
