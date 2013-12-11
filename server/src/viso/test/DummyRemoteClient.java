package viso.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DummyRemoteClient{
	public DummyRemoteClient(){}
	public static void main(String args[]){
		try {
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 12345);
			TestRemote server = (TestRemote) registry.lookup(
			"DummyRemoteTest");	
			for(int i=0;i<100;i+=1){
				server.testRemote(""+i);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		   
	}
}

