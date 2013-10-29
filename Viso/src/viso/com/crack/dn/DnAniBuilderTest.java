package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import viso.com.table.Table;
import viso.com.table.Table.PrefixTabStr;

import junit.framework.TestCase;

public class DnAniBuilderTest extends TestCase {

	public void testBuildTable() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\ac_skill1.ani", "r");
		Table dnMesh = (new DnAniBuilder()).BuildTable(file);
		file.close();
		
//		RandomAccessFile output = new RandomAccessFile("D:\\DnAniBuilderTestOut", "rw");
//		
//		PrefixTabStr.clear();
//		output.writeBytes("Header :: \n"+dnMesh.getTable("header").toString());
//		PrefixTabStr.clear();
//		output.writeBytes("animateNameArray :: \n"+dnMesh.getTable("animateNameArray").toString());
//		PrefixTabStr.clear();
//		output.writeBytes("animateFrameArray :: \n"+dnMesh.getTable("animateFrameArray").toString());
//		
		List<Object> boneInfoArray = dnMesh.getTable("boneInfoArray").toArray();
		int i = 0;
		
		Map<String, Set<String>> parentChildren = new HashMap<String, Set<String>>(); 
		for(Object obj : boneInfoArray){
			Table boneInfo = ((Table)obj);
			String boneName = boneInfo.getString("parentName");
			String childName = boneInfo.getString("boneName");
			if(parentChildren.containsKey(childName)){
				parentChildren.get(childName).add(boneName);
			}else{
				Set<String> nameSet = new HashSet<String>();
				nameSet.add(boneName);
				parentChildren.put(childName, nameSet);
			}
//			output.writeBytes("boneInfo ["+(i++)+"]:: \n"+boneInfo.toString());
		}
		System.out.println(parentChildren.toString());
//		output.close();
	}

}
