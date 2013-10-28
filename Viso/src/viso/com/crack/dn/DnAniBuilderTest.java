package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import viso.com.table.Table;
import viso.com.table.Table.PrefixTabStr;

import junit.framework.TestCase;

public class DnAniBuilderTest extends TestCase {

	public void testBuildTable() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\ac_skill1.ani", "r");
		Table dnMesh = (new DnAniBuilder()).BuildTable(file);
		file.close();
		PrefixTabStr.clear();
		System.out.println("Header :: \n"+dnMesh.getTable("header").toString());
		PrefixTabStr.clear();
		System.out.println("animateNameArray :: \n"+dnMesh.getTable("animateNameArray").toString());
		PrefixTabStr.clear();
		System.out.println("animateFrameArray :: \n"+dnMesh.getTable("animateFrameArray").toString());
		
		List<Object> boneInfoArray = dnMesh.getTable("boneInfoArray").toArray();
		int i = 0;
		for(Object obj : boneInfoArray){
			System.out.println("boneInfo ["+(i++)+"]:: \n"+((Table)obj).toString());
		}
	}

}
