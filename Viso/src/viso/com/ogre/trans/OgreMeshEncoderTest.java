package viso.com.ogre.trans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import viso.com.table.Table;
import viso.com.table.TableXmlEncoder;
import junit.framework.TestCase;

public class OgreMeshEncoderTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPrintElementTable() {
//		fail("Not yet implemented");
		Document doc = DocumentHelper.createDocument();  

		Element node = doc.addElement("mesh");
		Table table = OgreMeshFile.buildEmptyTable().getTable();
		
		OgreMeshEncoder.registerAll();
		TableXmlEncoder.print("OgreMeshEncoder", node, table);
		
		// 输出文件   
		File outputFile = new File("D:\\OgreMeshEncoderText.xml");  
		try {
			// 美化格式   
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 指定XML编码,不指定的话，默认为UTF-8   
			format.setEncoding("UTF-8");
			XMLWriter output = new XMLWriter(new FileWriter(outputFile), format);
			output.write(doc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}  

	}

}
