package viso.com.ogre.trans;

import java.util.List;

import org.dom4j.Element;

import viso.com.table.TableXmlEncoder;

class BoneAssignmentsEncoder extends TableXmlEncoder{
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		List<Object> vertexIndexArray = table.getTable("vertexIndexArray").repeatElements();
		List<Object> boneIndexArray = table.getTable("boneIndexArray").repeatElements();
		List<Object> weightArray = table.getTable("weightArray").repeatElements();
		
		if(vertexIndexArray==null)return node;
		
		Element vertexboneassignment = null;
		
		for(int i=0;i<vertexIndexArray.size();i++){
			vertexboneassignment = appendChild("vertexboneassignment");
			addArributesTo(vertexboneassignment, "vertexindex", vertexIndexArray.get(i));
			addArributesTo(vertexboneassignment, "boneindex", boneIndexArray.get(i));
			addArributesTo(vertexboneassignment, "weight", weightArray.get(i));
		}
		
		return node;
	}
	
}

class VertexBuffer0Encoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		List<Object> positionArray = table.getTable("positionArray").repeatElements();
		List<Object> normalArray = table.getTable("normalArray").repeatElements();
		
		if(positionArray==null || normalArray==null) return node;
		
		Element vertex = null;
		Element position = null;
		Element normal = null;
		
		for(int i=0; i<positionArray.size(); i+=3){
			
			if(i%3==0){
				vertex = appendChild("vertex");
				position = appendChildTo(vertex, "position");
				normal = appendChildTo(vertex, "normal");
			}
			
			addArributesTo(position, "x", positionArray.get(i));
			addArributesTo(position, "y", positionArray.get(i+1));
			addArributesTo(position, "z", positionArray.get(i+2));
			
			addArributesTo(normal, "x", normalArray.get(i));
			addArributesTo(normal, "y", normalArray.get(i+1));
			addArributesTo(normal, "z", normalArray.get(i+2));
			
		}
		
		return node;
	}
	
}

class VertexBuffer1Encoder extends TableXmlEncoder{
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		List<Object> vertexArray = table.getTable("vertexArray").repeatElements();
		
		Element vertex = null;
		Element texcoord = null;
		int count = 0;
		
		for(Object obj : vertexArray){
			if((count%2)==0) vertex = appendChild("vertext");
			texcoord = appendChildTo(vertex, "texcoord");
			if((count%2)==0){
				addArributesTo(texcoord, "u", obj);
			}else{
				addArributesTo(texcoord, "v", obj);
			}
			count++;
		}
		
		return node;
	}
	
}

class GeometryEncoder extends TableXmlEncoder{
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		
		
		return node;
	}
	
}

class FacesEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		List<Object> list = table.repeatElements();
		if(list == null) return node;
		
		int counter = 0;
		Element tmp = null;
		String attribute;
		
		for(Object obj : list){
			if((counter%3)==0){
				tmp = appendChild("face");
			}
			attribute = "v"+(1+(counter%3));
			if(obj instanceof Integer){
				addArributesTo(tmp, attribute, obj);
			}else{
				throw new IllegalStateException(" ¿‡–Õ≤ª∆•≈‰ ");
			}
			counter++;
		}
		return node;
	}
	
}

class SubMeshEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		
		return node;
	}
	
}

class SubmeshesEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		printChild("SubMeshEncoder", "submesh");
		return node;
	}
	
}

public class OgreMeshEncoder extends TableXmlEncoder{

	private static boolean OgreMeshEncoderInitOnce = false;
	
	public OgreMeshEncoder(){
		super();
	}
	
	public static void registerAll() {
		if(OgreMeshEncoderInitOnce){
			return;
		}
		TableXmlEncoder.registerAll();
		register("OgreMeshEncoder", new OgreMeshEncoder());
		register("SubmeshesEncoder", new SubmeshesEncoder());
		register("SubMeshEncoder", new SubMeshEncoder());
		register("FacesEncoder", new FacesEncoder());
		register("GeometryEncoder", new GeometryEncoder());
		register("VertexBuffer1Encoder", new VertexBuffer1Encoder());
		register("VertexBuffer0Encoder", new VertexBuffer0Encoder());
		register("BoneAssignmentsEncoder", new GeometryEncoder());
		OgreMeshEncoderInitOnce = true;
	}
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		printChild("SubmeshesEncoder", "submeshes");
		printChild("skeletonlink");
		return node;
	}
	
}
