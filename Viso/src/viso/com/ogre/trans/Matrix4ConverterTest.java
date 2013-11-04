package viso.com.ogre.trans;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import viso.com.crack.dn.DnBuilder;
import viso.com.table.Table;
import viso.util.math.matrix.Matrix4;
import junit.framework.TestCase;

public class Matrix4ConverterTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBuildMatrix4() throws IOException {
		RandomAccessFile file = new RandomAccessFile("D:\\academic.msh", "r");
		Table dnMesh = (new DnBuilder()).BuildTable(file);
		file.close();
		
		RandomAccessFile output = new RandomAccessFile("D:\\testBuildMatrix4", "rw");
		List<Object> boneInfoArray = dnMesh.getTable("boneInfoArray").repeatElements();
		// just test 3
		Table boneInfo;
		Object obj;
		Table m;
		List<Object> fList;
		List<Double> dList = new ArrayList<Double>();
		List<Matrix4> matrixs = new ArrayList<Matrix4>();
		
		for(int i=0;i<boneInfoArray.size() && i<1;i++){
			obj = boneInfoArray.get(i);
			boneInfo = (Table)obj;
			dList.clear();
			for(int j=1;j<=4;j++){
				m = boneInfo.getTable("M"+j);
				fList = m.repeatElements();
				for(int k=0;k<4;k++){
					dList.add(new Double((Float)fList.get(k)));
				}
			}
			matrixs.add(Matrix4Converter.buildMatrix4(dList));
		}
		
		for(Matrix4 m4 : matrixs){
			output.writeBytes("\nm4 is :\n"+m4.toString());
		}
		
		output.writeBytes("==== before build  ====\n");
		
		List<OgreBoneMatrix4> boneList = new ArrayList<OgreBoneMatrix4>();
		for(Matrix4 m4 : matrixs){
			OgreBoneMatrix4 bone = new OgreBoneMatrix4(m4);
			boneList.add(bone);
			output.writeBytes(bone.buildTable().toString());
		}
		output.writeBytes("\n==== after convert  ====");
		
		for(OgreBoneMatrix4 bone : boneList){
			output.writeBytes("\nm4 is :\n"+bone.toMatrix4().toString());
		}
		
		output.close();
	}

}
