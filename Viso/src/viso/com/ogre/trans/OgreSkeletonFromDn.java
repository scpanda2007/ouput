package viso.com.ogre.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import viso.com.table.Table;

public class OgreSkeletonFromDn {
	public static Table convertTable(final Table ogreSkeleton,
			final Table dnmesh, Table animesh) {

		ogreSkeleton.MapString("blendmode", "average");

		Map<String, String> parentChildren = new HashMap<String, String>();
		{
			List<Object> boneInfoArray = animesh.getTable("boneInfoArray")
					.toArray();
			for (Object obj : boneInfoArray) {
				Table boneInfo = ((Table) obj);
				String boneName = boneInfo.getString("parentName");
				String childName = boneInfo.getString("boneName");
				parentChildren.put(childName, boneName);
			}
		}

		List<Object> dnBoneArray = dnmesh.getTable("boneInfoArray")
				.repeatElements();
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

			for (int i = 0; i < dnBoneArray.size(); i++) {

				obj = dnBoneArray.get(i);
				ogrBone = (Table) obj;
				dList.clear();
				for (int j = 1; j <= 4; j++) {
					m = ogrBone.getTable("M" + j);
					fList = m.repeatElements();
					for (int k = 0; k < 4; k++) {
						dList.add(new Double((Float) fList.get(k)));
					}
				}
				boneInfo = (Matrix4Converter.buildOgreBoneMatrixTable(dList));
				boneArray.PutObject(boneInfo);
				boneInfo.MapInt("id", i);
				boneInfo.MapString("name", ogrBone.getString("boneName"));

				childParent = Table.createTable("childParent");
				childParent.MapString("bone", ogrBone.getString("boneName"));
				childParent.MapString("parent",
						parentChildren.get(ogrBone.getString("boneName")));
				parentArray.PutObject(childParent);
			}
		}

		List<Object> boneInfoArray = animesh.getTable("boneInfoArray")
				.repeatElements();
		List<Object> animateNameArray = animesh.getTable("animateNameArray")
				.repeatElements();
		Table aniArray = ogreSkeleton.MapAndCreateArray("animationArray");
		// List<Object> animateFrameArray =
		// animesh.getTable("animateFrameArray")
		// .repeatElements();

		final int version = animesh.getTable("header").getInt("version");

		{

			for (int i = 0; i < animateNameArray.size() && i < 1; i++) {// TODO:
																		// just
																		// test
				Table animation = Table.createTable("animation");
				aniArray.PutObject(animation);
				animation.MapString("name", (String) animateNameArray.get(i));
				Table trackArray = animation.MapAndCreateArray("trackArray");

				for (int j = 0; j < boneInfoArray.size(); j++) {//TODO: just test
					Table track = Table.createTable("track");
					trackArray.PutObject(track);

					Table keyframeTimeArray = track
							.MapAndCreateArray("keyframeTimeArray");
					Table translateArray = track
							.MapAndCreateArray("translateArray");
					Table rotateArray = track.MapAndCreateArray("rotateArray");

					Table boneInfoX = (Table) boneInfoArray.get(j);
					track.MapString("bone", boneInfoX.getString("boneName"));

					List<Object> NestedBoneArray = boneInfoX.getTable(
							"NestedBoneArray").repeatElements();
					// for(int k=0; k<NestedBoneArray.size(); k++){
					assert i < NestedBoneArray.size();

					Table nestedBoneInfo = (Table) NestedBoneArray.get(i);

					List<Object> transformation = nestedBoneInfo.getTable(
							"transformation").repeatElements();
					List<Object> rotation = nestedBoneInfo.getTable("rotation")
							.repeatElements();

					List<Object> transformFrameList = nestedBoneInfo
							.getTable("transformationInfo")
							.getTable("transformFrameArray").repeatElements();
					List<Object> transformList = nestedBoneInfo
							.getTable("transformationInfo")
							.getTable("transformationDataArray")
							.repeatElements();
					List<Object> rotationList = nestedBoneInfo
							.getTable("rotationInfo")
							.getTable("transformationDataArray")
							.repeatElements();
					List<Object> rotationFrameList = nestedBoneInfo
							.getTable("rotationInfo")
							.getTable("rotationFrameArray").repeatElements();
					int number = transformFrameList.size() > rotationFrameList
							.size() ? transformFrameList.size()
							: rotationFrameList.size();
					List<Object> frameList = transformFrameList.size() > rotationFrameList
							.size() ? transformFrameList : rotationFrameList;

					System.out.println("" + transformFrameList.size() + ":"
							+ rotationFrameList.size() + ":"
							+ transformList.size() + ":" + rotationList.size());
					
					int lastFrame = 0;
					int tick = 0;
					if(transformFrameList.size() == 2){
						lastFrame = (Integer)transformFrameList.get(1);
					}
					
					for (int l = 0; l < number; l++) {

						keyframeTimeArray.PutObject(new Float((1.0d / 24)
								* (Integer) frameList.get(l)));
						if (transformFrameList.size() == 2){
							tick = (Integer) frameList.get(l);
							translateArray.PutObject(
									((Float) transformList.get(0)+(Float)transformList.get(3))*tick/lastFrame);
							translateArray.PutObject(
									((Float) transformList.get(1)+(Float)transformList.get(4))*tick/lastFrame);
							translateArray.PutObject(
									((Float) transformList.get(2)+(Float)transformList.get(5))*tick/lastFrame);
						}else if (transformFrameList.size() == 0){
							translateArray.PutObject(new Float(
									(Float) transformation.get(0)));
							translateArray.PutObject(new Float(
									(Float) transformation.get(1)));
							translateArray.PutObject(new Float(
									(Float) transformation.get(2)));
						}else if (transformFrameList.size() > 0) {
							if(transformList.size() <= (l*3+2)){
								System.out.println("XXX"+boneInfoX.getString("boneName"));
							}
							translateArray.PutObject(new Float(
									(Float) transformList.get(l * 3 + 0)));
							translateArray.PutObject(new Float(
									(Float) transformList.get(l * 3 + 1)));
							translateArray.PutObject(new Float(
									(Float) transformList.get(l * 3 + 2)));
						}

						if (rotationFrameList.size() > 0) {
							if (version == 11) {
								rotateArray.PutObject(new Integer(
										(Integer) rotationList.get(l * 4 + 0)));
								rotateArray.PutObject(new Integer(
										(Integer) rotationList.get(l * 4 + 1)));
								rotateArray.PutObject(new Integer(
										(Integer) rotationList.get(l * 4 + 2)));
								rotateArray.PutObject(new Integer(
										(Integer) rotationList.get(l * 4 + 3)));
							} else {
								rotateArray.PutObject(new Float(
										(Float) rotationList.get(l * 4 + 0)));
								rotateArray.PutObject(new Float(
										(Float) rotationList.get(l * 4 + 1)));
								rotateArray.PutObject(new Float(
										(Float) rotationList.get(l * 4 + 2)));
								rotateArray.PutObject(new Float(
										(Float) rotationList.get(l * 4 + 3)));
							}
						} else {
							rotateArray.PutObject(new Float((Float) rotation
									.get(0)));
							rotateArray.PutObject(new Float((Float) rotation
									.get(1)));
							rotateArray.PutObject(new Float((Float) rotation
									.get(2)));
							rotateArray.PutObject(new Float((Float) rotation
									.get(3)));
						}
					}
				}
			}
		}

		return ogreSkeleton;
	}
}
