package viso.util.tools;

import java.nio.ByteBuffer;

public class MessageBuffer {
	
	final ByteBuffer buffer;
	
	public MessageBuffer(int size_){
		buffer = ByteBuffer.allocate(size_);
	}
	
	public MessageBuffer putString(String string){
		buffer.put(string.getBytes());
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
}
