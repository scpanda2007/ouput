package viso.test.framework.service.net;

import viso.util.tools.MessageBuffer;
import junit.framework.TestCase;

public class TestAsynMessageBuffer extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPutString() {
		fail("Not yet implemented");
	}

	public void testPutByte() {
		fail("Not yet implemented");
	}

	public void testPutBytes() {
		fail("Not yet implemented");
	}

	public void testPutInt() {
		fail("Not yet implemented");
	}

	public void testPutFloat() {
		fail("Not yet implemented");
	}

	public void testGetSize() {
		fail("Not yet implemented");
	}

	public void testGetBuffer() {
		fail("Not yet implemented");
	}

	public void testGetByte() {
		fail("Not yet implemented");
	}

	public void testLimit() {
		fail("Not yet implemented");
	}

	public void testPosition() {
		fail("Not yet implemented");
	}

	public void testGetBytes() {
//		fail("Not yet implemented");
		MessageBuffer buffer = new MessageBuffer(1024);
		byte[] sendbytes = new byte[24];
		for(int i=0;i<24;i++){
			sendbytes[i] = (byte)i;
		}
		buffer.putBytes(sendbytes);
		buffer.flip();
		byte[] recievebytes = buffer.getBytes(24);
		for(int i=0;i<24;i++){
			assertEquals(recievebytes[i] , sendbytes[i]);
		}
	}

	public void testGetString() {
//		fail("Not yet implemented");
		MessageBuffer buffer = new MessageBuffer(1024);
		buffer.putString(" this is a string. ");
		buffer.flip();
		System.out.println(buffer.getString());
	}

	public void testGetInt() {
		fail("Not yet implemented");
	}

	public void testGetShort() {
		fail("Not yet implemented");
	}

	public void testCapacity() {
		fail("Not yet implemented");
	}

	public void testPutShort() {
		fail("Not yet implemented");
	}

	public void testFlip() {
		fail("Not yet implemented");
	}

}
