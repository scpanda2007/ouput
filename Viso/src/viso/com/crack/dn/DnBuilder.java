package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;

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
		return boneInfo;
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
		
		meshInfo.MapTable("vertexindex", (new DnIntVectorBuilder(indexCount, 2)).BuildTable(file));
		MapAndBuildArray(meshInfo, "vertexDataArray", vertextCount, new DnFloatVectorBuilder(3));
		MapAndBuildArray(meshInfo, "normalDataArray", vertextCount, new DnFloatVectorBuilder(3));
		MapAndBuildArray(meshInfo, "UVDataArray", vertextCount, new DnFloatVectorBuilder(2));
		MapAndBuildArray(meshInfo, "boneIndexArray", vertextCount, new DnIntVectorBuilder(4, 2));
		MapAndBuildArray(meshInfo, "boneWeightArray", vertextCount, new DnFloatVectorBuilder(4));
		
		final int boneCount = GetInt(4);
		meshInfo.MapTable("boneNameArray", (new DnStringVectorBuilder(boneCount, 256)).BuildTable(file));
		
		// TODO Auto-generated method stub
		return meshInfo;
	}
	
}

public class DnBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table dnmesh = Table.createTable("DnMesh");
		dnmesh.MapTable("header", (new DnHeaderBuilder()).BuildTable(file));
		// bone info
		MapAndBuildArray(dnmesh, "boneInfoArray", 
				dnmesh.getTable("header").getInt("boneCount"), new DnBoneBuilder());
		// mesh info
		MapAndBuildArray(dnmesh, "meshInfoArray", 
				dnmesh.getTable("header").getInt("meshCount"), new DnMeshBuilder());
		return dnmesh;
	}

}
