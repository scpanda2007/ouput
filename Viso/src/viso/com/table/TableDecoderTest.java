/**
 * 
 */
package viso.com.table;

import java.io.IOException;
import java.io.RandomAccessFile;

import junit.framework.TestCase;

/**
 * @author delljy
 *
 */
public class TableDecoderTest extends TestCase {

	private TableDecoder _decoder = new TableDecoder();
	
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
//		File testfile = new File("tabledctest");
//		BufferedWriter bw = new  BufferedWriter(new FileWriter(testfile, true));
//		bw.write(strComp, 0, 255);
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#DecodeString(java.io.RandomAccessFile, int)}.
	 * @throws IOException 
	 */
	public void testDecodeString() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\academic.msh", "rw");
		System.out.println((new TableDecoder()).DecodeString(file, 256));
		file.close();
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#DecodeFloat(java.io.RandomAccessFile, int)}.
	 */
	public void testDecodeFloat() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#DecodeInt(java.io.RandomAccessFile, int)}.
	 */
	public void testDecodeInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#ReadToBuffer32(java.io.RandomAccessFile, int)}.
	 */
	public void testReadToBuffer32() {
		fail("Not yet implemented");
		
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#CopyToBuffer32(int, byte[])}.
	 */
	public void testCopyToBuffer32IntByteArray() {
		for(int number=0; number <=4 ;number ++){
			byte[] testScource = new byte[number];
			for(int i=1;i<number;i+=1){
				testScource[i] = (byte)i;
			}
			_decoder.CopyToBuffer32(number, false, testScource);
			for(int i=0;i<number;i+=1){
				assertEquals( "²âÊÔ ×Ö½Ú¿½±´ Ë³Ðò", testScource[i], _decoder.getByteOfBuffer32(i));
			}
			_decoder.CopyToBuffer32(number, true, testScource);
			for(int i=0;i<number;i+=1){
				assertEquals( "²âÊÔ ×Ö½Ú¿½±´ ÄæÐò", testScource[i], _decoder.getByteOfBuffer32(number-1-i));
			}
		}
	}

	/**
	 * Test method for {@link viso.com.table.TableDecoder#CopyToBuffer32(int, boolean, byte[])}.
	 */
	public void testCopyToBuffer32IntBooleanByteArray() {
		testCopyToBuffer32IntByteArray();
	}

}
