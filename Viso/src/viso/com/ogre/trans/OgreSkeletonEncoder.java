package viso.com.ogre.trans;

import java.util.List;

import org.dom4j.Element;

import viso.com.table.Table;
import viso.com.table.TableXmlEncoder;

class RotationEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		addAttributes("angle", table.getFloat("angle"));
		Element axis = appendChild("axis");
		addArributesTo(axis, "x", table.getFloat("x"));
		addArributesTo(axis, "y", table.getFloat("y"));
		addArributesTo(axis, "z", table.getFloat("z"));
		return node;
	}
	
}

class BoneEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		printChild("position");
		printChild("RotationEncoder", "rotation");
		return node;
	}
	
}

class BoneArrayEncoder extends TableXmlEncoder {

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		List<Object> boneArray = table.repeatElements();
		for(Object obj : boneArray){
			if(obj instanceof Table){
				print("BoneEncoder", appendChild("bone"), (Table)obj);
				continue;
			}
			throw new IllegalStateException(" 类型不匹配 ");
		}
		return node;
	}
	
}

class TrackEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		
		Element keyframes = appendChild("keyframes");
		
		List<Object> keyframeTimeArray = table.getTable("keyframeTimeArray").repeatElements();
		List<Object> translateArray = table.getTable("translateArray").repeatElements();
		List<Object> rotateArray = table.getTable("rotateArray").repeatElements();
		
		Element keyframe = null;
		Element translate = null;
		Element rotate = null;
		for(int i=0;i<keyframeTimeArray.size();i++){
			
			keyframe = appendChildTo(keyframes, "keyframe");
			translate = appendChildTo(keyframe, "translate");
			rotate = appendChildTo(keyframe, "rotate");
			
			addArributesTo(keyframe, "time", keyframeTimeArray.get(i));
			
			addArributesTo(translate, "x", translateArray.get(i*3));
			addArributesTo(translate, "y", translateArray.get(i*3+1));
			addArributesTo(translate, "z", translateArray.get(i*3+2));
			
			addArributesTo(rotate, "rotate", rotateArray.get(i*4));
			addArributesTo(rotate, "x", rotateArray.get(i*4+1));
			addArributesTo(rotate, "y", rotateArray.get(i*4+2));
			addArributesTo(rotate, "z", rotateArray.get(i*4+3));
		}
		
		return node;
	}
	
}

class AnimationEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		Table tracks = table.getTable("trackArray");
		List<Object> trackArray = tracks.repeatElements();
		for(Object obj : trackArray){
			if(obj instanceof Table){
				print("TrackEncoder", appendChild("track"), (Table)obj);
				continue;
			}
			throw new IllegalStateException(" 类型不匹配 ");
		}
		
		return node;
	}
	
}

class AnimationArrayEncoder extends TableXmlEncoder{

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		List<Object> animationArray = table.repeatElements();
		for(Object obj : animationArray){
			if(obj instanceof Table){
				print("AnimationEncoder", appendChild("animation"), (Table)obj);
				continue;
			}
			throw new IllegalStateException(" 类型不匹配 ");
		}
		
		return node;
	}
	
}

class BoneHierarchyEncoder extends TableXmlEncoder {

	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		List<Object> boneparentArray = table.repeatElements();
		for(Object obj : boneparentArray){
			if(obj instanceof Table){
				print("default", appendChild("boneparent"), (Table)obj);
				continue;
			}
			throw new IllegalStateException(" 类型不匹配 ");
		}
		return node;
	}
	
}

public class OgreSkeletonEncoder extends TableXmlEncoder {

	private static boolean AnimationEncoderInitOnce = false;
	
	public static void registerAll(){
		if(AnimationEncoderInitOnce)return;
		
		TableXmlEncoder.registerAll();
		register("TrackEncoder", new TrackEncoder());
		register("BoneArrayEncoder", new BoneArrayEncoder());
		register("BoneEncoder", new BoneEncoder());
		
		register("RotationEncoder", new RotationEncoder());
		register("OgreSkeletonEncoder", new OgreSkeletonEncoder());
		register("BoneHierarchyEncoder", new BoneHierarchyEncoder());
		
		register("AnimationArrayEncoder", new AnimationArrayEncoder());
		register("AnimationEncoder", new AnimationEncoder());
		
		AnimationEncoderInitOnce = true;
	}
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		print("BoneArrayEncoder", appendChild("bones"), table.getTable("boneArray"));
		print("BoneHierarchyEncoder", appendChild("bonehierarchy"), table.getTable("boneParentArray"));
		print("AnimationArrayEncoder", appendChild("animations"), table.getTable("animationArray"));
		return node;
	}

}
