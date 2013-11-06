package viso.util.tools;

import java.nio.ByteBuffer;

public class MessageBuffer {
	
	final ByteBuffer buffer;
	
	public MessageBuffer(int size_){
		buffer = ByteBuffer.allocate(size_);
	}
	
	public MessageBuffer(byte[] payload) {
		// TODO Auto-generated constructor stub
		buffer = ByteBuffer.wrap(payload);
	}

	public MessageBuffer putString(String string){
		buffer.put(string.getBytes());
		return this;
	}
	
	public MessageBuffer putByte(byte byte_){
		buffer.put(byte_);
		return this;
	}
	
	public MessageBuffer putBytes(byte arg0[]){
		buffer.put(arg0);
		return this;
	}
	
	public MessageBuffer putInt(int number){
		buffer.putInt(number);
		return this;
	}
	
	public MessageBuffer putFloat(float float_){
		buffer.putFloat(float_);
		return this;
	}
	
	public static int getSize(String arg0){
		return arg0.getBytes().length;
	}

	public byte[] getBuffer() {
		// TODO Auto-generated method stub
		return buffer.array();
	}

	public byte getByte() {
		// TODO Auto-generated method stub
		return buffer.get();
	}

	public int limit() {
		// TODO Auto-generated method stub
		return buffer.limit();
	}

	public int position() {
		// TODO Auto-generated method stub
		return buffer.position();
	}

	public byte[] getBytes(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getShort() {
		// TODO Auto-generated method stub
		return buffer.getShort();
	}

	public int capacity() {
		// TODO Auto-generated method stub
		return buffer.capacity();
	}

	public void putShort(int arg0) {
		// TODO Auto-generated method stub
		buffer.putShort((short)arg0);
	}
}