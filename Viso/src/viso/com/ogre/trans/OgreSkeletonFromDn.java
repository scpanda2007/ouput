package viso.com.ogre.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import viso.com.table.Table;

public class OgreSkeletonFromDn {
	public static Table convertTable(final Table ogreSkeleton, final Table dnmesh, Table animesh){
		
		ogreSkeleton.MapString("blendmode", "average");
		
		Map<String, String> parentChildren = new HashMap<String, String>();
		{
			List<Object> boneInfoArray = animesh.getTable("boneInfoArray").toArray();
			for(Object obj : boneInfoArray){
				Table boneInfo = ((Table)obj);
				String boneName = boneInfo.getString("parentName");
				String childName = boneInfo.getString("boneName");
				parentChildren.put(childName, boneName);
			}
		}
		
		List<Object> dnBoneArray = dnmesh.getTable("boneInfoArray").repeatElements();
		Table parentArray = ogreSkeleton.MapAndCreateArray("boneParentArray");
		Table boneArray = ogreSkeleton.MapAndCreateArray("boneArray");
		{
			Table boneInfo;
			Table childParent;
			Table ogrBone;
			Object obj;
			Table m;
			
			List<Object> fList;
			List<Double> dList = new ArrayList<Double>();
			
			for(int i=0;i<dnBoneArray.size();i++){
				
				obj = dnBoneArray.get(i);
				ogrBone = (Table)obj;
				dList.clear();
				for(int j=1;j<=4;j++){
					m = ogrBone.getTable("M"+j);
					fList = m.repeatElements();
					for(int k=0;k<4;k++){
						dList.add(new Double((Float)fList.get(k)));
					}
				}
				boneInfo = (Matrix4Converter.buildOgreBoneMatrixTable(dList));
				boneArray.PutObject(boneInfo);
				boneInfo.MapInt("id", i);
				boneInfo.MapString("name", ogrBone.getString("boneName"));
				
				childParent = Table.createTable("childParent");
				childParent.MapString("bone", ogrBone.getString("boneName"));
				childParent.MapString("parent", parentChildren.get(ogrBone.getString("boneName")));
				parentArray.PutObject(childParent);
			}
		}
		
		List<Object> boneInfoArray = animesh.getTable("boneInfoArray").repeatElements();
		List<Object> animateNameArray = animesh.getTable("animateNameArray").repeatElements();
		Table aniArray = ogreSkeleton.MapAndCreateArray("animationArray");
		List<Object> animateFrameArray = animesh.getTable("animateFrameArray").repeatElements();
		
		final int version = animesh.getTable("header").getInt("version");
		
		{
			Table animation = Table.createTable("animation");
			for(int i=0;i<animateNameArray.size();i++){
				animation.MapString("name", (String)animateNameArray.get(i));
				Table trackArray = animation.createArray("trackArray");
				
				for(int j=0;j<boneInfoArray.size();j++){
					Table track = Table.createTable("track");
					trackArray.PutObject(track);
					
					Table keyframeTimeArray = track.MapAndCreateArray("keyframeTimeArray");
					Table translateArray = track.MapAndCreateArray("translateArray");
					Table rotateArray = track.MapAndCreateArray("rotateArray");
					
					Table boneInfoX = (Table)boneInfoArray.get(j);
					track.MapString("bone", boneInfoX.getString("boneName"));
					
					List<Object> NestedBoneArray = boneInfoX.getTable("NestedBoneArray").repeatElements();
					for(Object obj : NestedBoneArray){
						
					}
				}
			}
		}
		
		return  ogreSkeleton;
	}
}
