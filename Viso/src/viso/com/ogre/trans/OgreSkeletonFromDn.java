package viso.com.ogre.trans;

import java.util.List;

import viso.com.table.Table;

public class OgreSkeletonFromDn {
	public static Table convertTable(final Table ogreSkeleton, final Table dnmesh){
		
		List<Object> dnBoneArray = dnmesh.getTable("boneInfoArray").repeatElements();
		Table boneArray = ogreSkeleton.MapAndCreateArray("ogreSkeleton");
		{
			Table boneInfo;
			Table position;
			Table rotation;
			for(int i=0;i<dnBoneArray.size();i++){
				
				boneInfo = Table.createTable("boneInfo");
				boneArray.PutObject(boneInfo);
				boneInfo.MapInt("id", i);
				
				position = boneInfo.MapAndCreateTable("position");
				position.MapFloat("x", 0.0f);
				position.MapFloat("y", 0);
				position.MapFloat("z", 0);
				
				rotation = boneInfo.MapAndCreateTable("rotation");
				rotation.MapFloat("angle", 0.0f);
				rotation.MapFloat("x", 0.0f);
				rotation.MapFloat("y", 0.0f);
				rotation.MapFloat("z", 0.0f);
			}
		}
		
		return  ogreSkeleton;
	}
}
