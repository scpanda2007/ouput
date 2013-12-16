package viso.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import viso.sbeans.framework.net.test.DummyClient;

public class Test {
	
	public static void testZeroUTF() {
		byte test[] = new byte[0];
		DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(test));
		try {
			String value = dis.readUTF();
			System.out.println("read a zero bytes we get string:" + value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printACos(int from,int to,int inc){
		ArrayList<Integer> angles = new ArrayList<Integer>();
		for(int i=from;i<=to;i+=inc){
			double radio = Math.acos(1.0f*i/100);
			angles.add(new Double(radio*180/Math.PI).intValue());
		}
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<angles.size();i+=1){
			if(i>0){
				buffer.append(",");
			}
			buffer.append(""+angles.get(i).intValue());
		}
		System.out.println(buffer.toString());
	}
	
	public static void printCos(int from,int to,int inc){
		ArrayList<Integer> angles = new ArrayList<Integer>();
		for(int i=from;i<=to;i+=inc){
			angles.add(new Double(Math.cos(i*2*Math.PI/360)*1000).intValue());
		}
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<angles.size();i+=1){
			if(i>0){
				buffer.append(",");
			}
			buffer.append(""+angles.get(i).intValue());
		}
		System.out.println(buffer.toString());
	}
	
	public static void main(String args[]){
		DummyClient cient = new DummyClient("client[0]");
		InetSocketAddress end = new InetSocketAddress("192.168.1.102",8000);
		cient.connectAndWait(end);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
