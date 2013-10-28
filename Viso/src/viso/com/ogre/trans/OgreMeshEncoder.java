package viso.com.ogre.trans;

import org.dom4j.Element;

import viso.com.table.TableXmlEncoder;

class SubMeshEncoder extends TableXmlEncoder{

	static {
		encoders.put("SubMeshEncoder", new SubMeshEncoder());
	}
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		return node;
	}
	
}

class SubmeshesEncoder extends TableXmlEncoder{

	static {
		encoders.put("SubmeshesEncoder", new SubmeshesEncoder());
	}
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		printChild("SubMeshEncoder", "submesh");
		return node;
	}
	
}

public class OgreMeshEncoder extends TableXmlEncoder{
	
	static {
		encoders.put("OgreMeshEncoder", new OgreMeshEncoder());
	}

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		printChild("SubmeshesEncoder", "submeshes");
		printChild("skeletonlink");
		return node;
	}
	
}
