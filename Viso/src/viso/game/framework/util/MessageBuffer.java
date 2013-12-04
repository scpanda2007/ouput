package viso.game.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 消息缓存
 * */
public class MessageBuffer {
	//数据缓存
	private ByteBuffer buffer;
	
	/**
	 * 生成一个用于写入的消息
	 * @param 预估大小
	 * */
	public MessageBuffer(int size){
		buffer = ByteBuffer.allocate(size);
	};
	
	
	/**
	 * 生成一个用于读数据的消息
	 * @param scr 想要解析的数据
	 * */
	public MessageBuffer(byte src[]){
		buffer = ByteBuffer.wrap(src);
	}
	
	public void writeBoolean(boolean value){
		if(value){
			buffer.put((byte)0);
			return;
		}
		buffer.put((byte)1);
	}
	
	public boolean readBoolean(){
		return buffer.get()==0;
	}
	
	public void writeByte(byte value){
		buffer.put(value);
	}
	
	public byte readByte(){
		return buffer.get();
	}
	
	public void writeShort(short value){
		buffer.putShort(value);
	}
	
	public short readShort(){
		return buffer.getShort();
	}
	
	public void writeInt(int value){
		buffer.putInt(value);
	}
	
	public int readInt(){
		return buffer.getInt();
	}
	
	public void writeLong(long value){
		buffer.putLong(value);
	}
	
	public long readLong(){
		return buffer.getLong();
	}
	
	public void writeFloat(float value){
		buffer.putFloat(value);
	}
	
	public float readFloat(){
		return buffer.getFloat();
	}
	
	public void writeBytes(byte[] src){
		buffer.putInt(src.length);
		buffer.put(src);
	}
	
	public byte[] readBytes(){
		int size = buffer.getInt();
		if(size==-1)return null;
		byte[] array = new byte[size];
		buffer.get(array, 0, array.length);
		return array;
	}
	
	public byte[] getBytes(int offset, int length){
		byte[] dst = new byte[length];
		buffer.get(dst, offset, length);
		return dst;
	}
	
	public ByteBuffer buffer(){
		return buffer;
	}
	
	public ByteBuffer duplicate(){
		return buffer.duplicate();
	}
	
	public void writeUTF(String str) throws IOException{
		if(str==null){
			buffer.putInt(-1);
			return;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeUTF(str);
		writeBytes(baos.toByteArray());
	}
	
	public String readUTF() throws IOException{
		byte[] array = readBytes();
		if(array==null)return null;
		
		ByteArrayInputStream bios = new ByteArrayInputStream(array);
		DataInputStream dos = new DataInputStream(bios);
		return dos.readUTF();
	}
	
	public int limit(){ return buffer.limit();}
	public int remaining(){return buffer.remaining();}
	public int position(){return buffer.position();}
	public int capacity(){return buffer.capacity();}
	
}
