package viso.sbeans.framework.store.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import viso.sbeans.framework.store.DataEncoder;

public class TestEncoder {
	@Test
	public void testValue(){
		long value = 0xdeadbeefL;
		assertEquals(value,DataEncoder.decodeLong(DataEncoder.encodeLong(value)));
	}
	
	@Test
	public void testVarInt(){
		int test[] = new int[]{0,0x7a,0x8a,0x7a7a,0x8a8a,0x7a7a7a,0x8a8a8a,0x7a7a7a7a,0x8a8a8a8a};
		for(int i=0;i<test.length;i+=1){
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				DataEncoder.encodeVarInt(test[i], dos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				DataInputStream dis = new DataInputStream(bais);
				assertEquals(test[i],DataEncoder.decodeVarInt(dis));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
