package viso.sbeans.framework.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageBuffer {
	ByteBuffer buffer;
	public MessageBuffer(int size){
		buffer = ByteBuffer.allocateDirect(size);
	}
	public MessageBuffer(byte payload[]){
		buffer = ByteBuffer.wrap(payload);
	}
	public ByteBuffer buffer(){
		return buffer;
	}
	public byte readByte(){
		return buffer.get();
	}
	public void writeByte(byte arg0){
		buffer.put(arg0);
	}
	public boolean readBoolean(){
		return buffer.get()==(byte)0;
	}
	public void writeBoolean(boolean arg0){
		if(arg0){
			buffer.put((byte)0);
			return;
		}
		buffer.put((byte)1);
	}
	public short readShort(){
		return buffer.getShort();
	}
	public void writeShort(short arg0){
		buffer.putShort(arg0);
	}
	public int readInt(){
		return buffer.getInt();
	}
	public void writeInt(int arg0){
		buffer.putInt(arg0);
	}
	public long readLong(){
		return buffer.getLong();
	}
	public void writeLong(long arg0){
		buffer.putLong(arg0);
	}
	public byte[] readBytes(){
		short size = buffer.getShort();
		if(size==-1)return null;
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		return bytes;
	}
	public void writeBytes(byte[] bytes){
		if(bytes==null){
			buffer.putShort((short)-1);
			return;
		}
		buffer.putShort((short)bytes.length);
		buffer.put(bytes);
	}
	public String readUTF(){
		byte[] bytes = readBytes();
		if(bytes==null)return null;
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			return dis.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void writeUTF(String str){
		if(str==null){
			buffer.putShort((short)-1);
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeUTF(str);
			writeBytes(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MessageBuffer flip(){ buffer.flip(); return this; }
}
