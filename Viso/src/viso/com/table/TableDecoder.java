package viso.com.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * ÎÄ¼þ½âÎö
 * */
public class TableDecoder {
	public int repeat = 1;
	public String name;
	public static boolean BigEndian = true;
	
	private byte[] readbuffer1 = new byte[1];
	private byte[] readbuffer2 = new byte[2];
	private byte[] readbuffer3 = new byte[3];
	private byte[] readbuffer4 = new byte[4];
	private byte[] buffer32 = new byte[4];
	private byte[] buffer16 = new byte[4];
	private byte[] buffer1024 = new byte[1024];
	private ByteBuffer buffer32Decoder = ByteBuffer.wrap(buffer32);
	private ByteBuffer buffer16Decoder = ByteBuffer.wrap(buffer16);
	
	public String DecodeString(RandomAccessFile file, int number) throws IOException{
		file.read(buffer1024, 0, number);
		String string = new String(buffer1024);
		return string.indexOf(0)==-1 ? string : string.substring(0, string.indexOf(0));
	}
	
	public float DecodeFloat(RandomAccessFile file, int number) throws IOException{
		ReadToBuffer32(file, number);
		return buffer32Decoder.getFloat();
	}
	
	public int DecodeInt(RandomAccessFile file, int number) throws IOException{
		ReadToBuffer32(file, number);
		return buffer32Decoder.getInt();
	}
	
	public short DecodeShort(RandomAccessFile file, int number) throws IOException{
		ReadToBuffer16(file, number);
		return buffer16Decoder.getShort();
	}

	public void ReadToBuffer32(RandomAccessFile file, int number) throws IOException{
		byte[] read = null;
		switch(number){
		case 1:read = readbuffer1;break;
		case 2:read = readbuffer2;break;
		case 3:read = readbuffer3;break;
		case 4:read = readbuffer4;break;
		default:throw new IllegalStateException();
		}
		file.readFully(read);
		CopyToBuffer32(number, read);
	}
	
	public void ReadToBuffer16(RandomAccessFile file, int number) throws IOException{
		byte[] read = null;
		switch(number){
		case 1:read = readbuffer1;break;
		case 2:read = readbuffer2;break;
		default:throw new IllegalStateException();
		}
		file.readFully(read);
		CopyToBuffer16(number, read);
	}
	
	public void CopyToBuffer32(int number, byte[] source){
		CopyToBuffer32(number, BigEndian, source);
	}
	
	public void CopyToBuffer16(int number, byte[] source){
		CopyToBuffer16(number, BigEndian, source);
	}
	
	public byte getByteOfBuffer32(int i){
		return buffer32[i];
	}
	
	public void CopyToBuffer32(int number, boolean reverse, byte[] source){
		buffer32Decoder.rewind();
		buffer32[0] = 0;
		buffer32[1] = 0;
		buffer32[2] = 0;
		buffer32[3] = 0;
		if(!reverse){
			System.arraycopy(buffer32, 0, source, 0, number);
		}else{
			for(int i=0;i<number;i++){
				buffer32[3-i] = source[i];
			}
		}
	}
	
	public void CopyToBuffer16(int number, boolean reverse, byte[] source){
		buffer16Decoder.rewind();
		buffer16[0] = 0;
		buffer16[1] = 0;
		if(!reverse){
			System.arraycopy(buffer16, 0, source, 0, number);
		}else{
			for(int i=0;i<number;i++){
				buffer16[1-i] = source[i];
			}
		}
	}

	public static boolean isBigEndian() {
		return BigEndian;
	}

	public static void setBigEndian(boolean bigEndian) {
		BigEndian = bigEndian;
	}
}
