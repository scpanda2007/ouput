package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.Table;

import junit.framework.TestCase;

public class DnBuilderTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInnterBuildTable() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\academic.msh", "rw");
		Table dnTable = (new DnBuilder()).BuildTable(file);
		file.close();
		Table header = dnTable.getTable("header");
		System.out.println(header.getString("header"));
		System.out.println(header.getInt("meshCount"));
		System.out.println(header.getInt("boneCount"));
		
	}

}
