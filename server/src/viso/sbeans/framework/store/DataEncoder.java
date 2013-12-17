package viso.sbeans.framework.store;

public class DataEncoder {
	public static int decodeInt30(byte[] data){
		return 0;
	}
	public static int decodeInt(byte[] data){
		return 0;
	}
	public static short decodeShort(byte[] data){
		return 0;
	}
	
	public static byte[] encodeShort(short number){
		return null;
	}
	public static byte[] encodeInt(int number){
		return null;
	}
	public static byte[] encodeInt30(int number){
		return null;
	}
	
	public static long decodeLong(byte[] bytes){
		if(bytes.length > 8){
			throw new IllegalArgumentException("Error param length.");
		}
		long n = (bytes[0] & 0xff) ^ 0x80;
		n <<= 8;
		n += (bytes[1] & 0xff);
		n <<= 8;
		n += (bytes[2] & 0xff);
		n <<= 8;
		n += (bytes[3] & 0xff);
		n <<= 8;
		n += (bytes[4] & 0xff);
		n <<= 8;
		n += (bytes[5] & 0xff);
		n <<= 8;
		n += (bytes[6] & 0xff);
		n <<= 8;
		n += (bytes[7] & 0xff);
		return n;
	}
	
	public static byte[] encodeLong(long number){
		byte bytes[] = new byte[8];
		bytes[0] = (byte)((number >>> 56) ^ 0x80);
		bytes[1] = (byte)(number >>> 48);
		bytes[2] = (byte)(number >>> 40);
		bytes[3] = (byte)(number >>> 32);
		bytes[4] = (byte)(number >>> 24);
		bytes[5] = (byte)(number >>> 16);
		bytes[6] = (byte)(number >>> 8);
		bytes[7] = (byte)(number);
		return bytes;
	}
}
