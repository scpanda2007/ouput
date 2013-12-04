package viso.sbeans.framework.net.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.*;
import viso.sbeans.framework.net.MessageBuffer;


public class TestMessageBuffer {

	private boolean checkFalse = false;
	private boolean checkTrue = true;
	private byte checkByte = (byte)0x0a;
	private short checkShort = (short)0x0a0b;
	private int checkInt = (int)0x0a0b0c0d;
	private long checkLong = (long) 0x0a0b0c0d0e0f0a0bl;
	
	private MessageBuffer message;
	
	@Before
	public void setUp(){
		message = new MessageBuffer(1024);
		Arrays.sort(new byte[0]);
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testCheckInt(){
		message.writeBoolean(checkFalse);
		message.writeBoolean(checkTrue);
		message.writeByte(checkByte);
		message.writeShort(checkShort);
		message.writeInt(checkInt);
		message.writeLong(checkLong);
		message.flip();
		assertEquals(message.readBoolean(), checkFalse);
		assertEquals(message.readBoolean(), checkTrue);
		assertEquals(message.readByte(), checkByte);
		assertEquals(message.readShort(), checkShort);
		assertEquals(message.readInt(), checkInt);
		assertEquals(message.readLong(), checkLong);
		
	}
	
	@Test
	public void testCheckZeroBytes(){
		message.writeBytes(new byte[0]);
		message.flip();
		byte array[] = message.readBytes();
		System.out.println("read a zero arrary. we get -->"+Arrays.asList(array)+" with length:"+array.length);
	}
	
	@Test
	public void testCheckEmptryString(){
		message.writeUTF("");
		message.flip();
		System.out.println("read empty string. we get -->"+message.readUTF());
	}
}
