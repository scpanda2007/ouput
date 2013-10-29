package viso.com.ogre.trans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import viso.com.crack.dn.DnBuilder;
import viso.com.table.Table;
import viso.com.table.TableXmlEncoder;
import junit.framework.TestCase;

public class OgreMeshFromDnTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConvertTable() throws IOException {
		Table dnmesh;
		{
			RandomAccessFile file = new RandomAccessFile("D:\\academic.msh", "r");
			dnmesh = (new DnBuilder()).BuildTable(file);
			file.close();
		}
		
		Table ogrMesh = Table.createTable("mesh");
		OgreMeshFromDn.convertTable(ogrMesh, dnmesh);
		RandomAccessFile output = new RandomAccessFile("D:\\OgreMeshFromDnTest", "rw");
		output.writeBytes(ogrMesh.toString());
		output.close();
		
		Document doc = DocumentHelper.createDocument();  

		Element node = doc.addElement("mesh");
		
		OgreMeshEncoder.registerAll();
		TableXmlEncoder.print("OgreMeshEncoder", node, ogrMesh);
		
		// 输出文件   
		File outputFile = new File("D:\\OgreMeshFromDnTest.xml");  
		try {
			// 美化格式   
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 指定XML编码,不指定的话，默认为UTF-8   
			format.setEncoding("UTF-8");
			XMLWriter output2 = new XMLWriter(new FileWriter(outputFile), format);
			output2.write(doc);
			output2.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
