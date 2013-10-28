package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.Table;
import viso.com.table.Table.PrefixTabStr;
import junit.framework.TestCase;

public class DnBuilderTest extends TestCase {

	public void testBuildTable() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\academic.msh", "r");
		Table dnMesh = (new DnBuilder()).BuildTable(file);
		file.close();
		
		RandomAccessFile output = new RandomAccessFile("D:\\DnBuilderTest", "rw");
		
		PrefixTabStr.clear();
		output.writeBytes(dnMesh.toString());
		
		output.close();
	}

}
