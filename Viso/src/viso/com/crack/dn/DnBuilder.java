package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;

class DnIntVectorBuilder extends DecodeItem {
	
	private int number = 0;
	private int intType = 0;
	public DnIntVectorBuilder(int number, int intType){
		this.number = number;
		this.intType = intType;
	}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException{
		// TODO Auto-generated method stub
		Table vector = Table.createArray("Unkown Name");
		for(int i=0;i<number;i++)
			vector.PutObject(new Integer(GetInt(intType)));
		return vector;
	}
	
}

class DnFloatVectorBuilder extends DecodeItem {
	
	private int number = 0;
	public DnFloatVectorBuilder(int number){this.number = number;}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException{
		// TODO Auto-generated method stub
		Table vector = Table.createArray("Unkown Name");
		for(int i=0;i<number;i++)
			vector.PutObject(new Float(GetFloat(4)));
		return vector;
	}
	
}

class DnHeaderBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException{
		// TODO Auto-generated method stub
		Table header = Table.createTable("Header");
		header.MapString("header", GetString(256));
		header.MapInt("version", GetInt(4));
		header.MapInt("meshCount", GetInt(4));
		header.MapInt("unknown1", GetInt(4));
		header.MapInt("unknown2", GetInt(4));
		header.MapTable("BoundingBoxMax", (new DnFloatVectorBuilder(3)).BuildTable(file));
		header.MapTable("BoundingBoxMin", (new DnFloatVectorBuilder(3)).BuildTable(file));
		header.MapInt("boneCount", GetInt(4));
		header.MapInt("unknown3", GetInt(4));
		header.MapInt("otherCount", GetInt(4));
		file.seek(1024);//¶¨Î»µ½1024
		return header;
	}
	
}

class DnBoneBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table boneInfo = Table.createTable("boneInfo");
		boneInfo.MapString("boneName", GetString(256));
		DnFloatVectorBuilder vectorFloat4 = new DnFloatVectorBuilder(4);
		boneInfo.MapTable("M1", vectorFloat4.BuildTable(file));
		boneInfo.MapTable("M2", vectorFloat4.BuildTable(file));
		boneInfo.MapTable("M3", vectorFloat4.BuildTable(file));
		boneInfo.MapTable("M4", vectorFloat4.BuildTable(file));
		return null;
	}
	
}

class DnMeshBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		
		Table meshInfo = Table.createTable("meshInfo");
		meshInfo.MapString("sceneRoot", GetString(256));
		meshInfo.MapString("meshName", GetString(256));
		
		final int vertextCount = GetInt(4);
		meshInfo.MapInt("vertexCount", vertextCount);
		final int indexCount = GetInt(4);
		meshInfo.MapInt("indexCount", indexCount);
		
		meshInfo.MapInt("unknown", GetInt(4));
		meshInfo.MapInt("renderMode", GetInt(4));
		
		file.skipBytes(512 - 16);
		
		Table vertexIndexArray = meshInfo.MapAndCreateArray("indexArray");
		for(int i=0; i<indexCount; i+=1){
			vertexIndexArray.PutObject(GetInt(2));
		}
		
		DnFloatVectorBuilder vectorFloat3Decoder = new DnFloatVectorBuilder(3);
		DnFloatVectorBuilder vectorFloat2Decoder = new DnFloatVectorBuilder(2);
		DnIntVectorBuilder vectorInt4_2Decoder = new DnIntVectorBuilder(4, 2);
		
		Table vertexDataArray = meshInfo.MapAndCreateArray("vertexIndexArray");
		for(int i=0;i<vertextCount;i++){
			vertexDataArray.PutObject(vectorFloat3Decoder.BuildTable(file));
		}
		
		Table normalDataArray = meshInfo.MapAndCreateArray("normalDataArray");
		for(int i=0;i<vertextCount;i++){
			normalDataArray.PutObject(vectorFloat3Decoder.BuildTable(file));
		}
		
		Table uvDataArray = meshInfo.MapAndCreateArray("UVDataArray");
		for(int i=0;i<vertextCount;i++){
			uvDataArray.PutObject(vectorFloat2Decoder.BuildTable(file));
		}
		
		Table boneIndexArray = meshInfo.MapAndCreateArray("boneIndexArray");
		for(int i=0;i<vertextCount;i++){
			boneIndexArray.PutObject(vectorInt4_2Decoder.BuildTable(file));
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
}

public class DnBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table dnmesh = Table.createTable("DnMesh");
		dnmesh.MapTable("header", (new DnHeaderBuilder()).BuildTable(file));
		// bone info
		DnBoneBuilder dnBoneBuilder = new DnBoneBuilder();
		Table boneInfoArray = Table.createArray("boneInfoArray");
		final int boneCount = dnmesh.getTable("header").getInt("boneCount");
		for(int i=0;i<boneCount;i++){
			boneInfoArray.PutObject(dnBoneBuilder.BuildTable(file));
		}
		return dnmesh;
	}

}
