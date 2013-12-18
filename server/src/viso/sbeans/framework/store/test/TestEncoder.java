package viso.sbeans.framework.store.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import viso.sbeans.framework.store.DataEncoder;

public class TestEncoder {
	@Test
	public void testValue(){
		long value = 0xdeadbeefL;
		assertEquals(value,DataEncoder.decodeLong(DataEncoder.encodeLong(value)));
	}
}
