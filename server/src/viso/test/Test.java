package viso.test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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
	
	public static void main(String args[]){
	}
}
