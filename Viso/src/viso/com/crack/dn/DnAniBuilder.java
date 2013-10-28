package viso.com.crack.dn;

import java.io.IOException;

import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;
import viso.com.table.Table.PrefixTabStr;

class DnAniHeaderDecoder extends DecodeItem{

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table header = Table.createTable("header");
		header.MapString("header", GetString(256));
		header.MapInt("version", GetInt(4));
		header.MapInt("boneCount", GetInt(4));
		header.MapInt("animateCount", GetInt(4));
		file.seek(1024);
		return header;
	}
	
}

class DnAniBoneInfoDecoder extends DecodeItem{

	private final int animateCount;
	private final int version;
	
	public DnAniBoneInfoDecoder(int animateCount, int version){
		this.animateCount = animateCount;
		this.version = version;
	}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table table = Table.createTable("DniAniBoneInfo");
		table.MapString("boneName", GetString(256));
		table.MapString("parentName", GetString(768));
		PrefixTabStr.clear();
		MapAndBuildArray(table, "NestedBoneArray", animateCount, (new DnAniNestedBoneDecoder(version)));
		return table;
	}
	
}

class DnAniNestedBoneDecoder extends DecodeItem {

	final int version;
	
	public DnAniNestedBoneDecoder(int version){
		this.version = version;
	}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table table = Table.createTable("NestedBoneInfo");
		table.MapTable("transformation", (new DnFloatVectorBuilder(3)).BuildTable(file));
		table.MapTable("rotation", (new DnFloatVectorBuilder(4)).BuildTable(file));
		table.MapTable("scaling", (new DnFloatVectorBuilder(3)).BuildTable(file));
		
		// transformation
		final int transformationCount = GetInt(4);
		table.MapInt("transformationCount", transformationCount);
		DecodeItem tableArrayInfoDecoder2 = new DecodeItem(){
			@Override
			public Table innterBuildTable(RandomAccessFile file)
					throws IOException {
				
				Table table = Table.createTable("transformInfo");
				Table array1 = table.MapAndCreateArray("transformFrameArray");
				Table array2 = table.MapAndCreateArray("transformationDataArray");
				
				for(int i=0;i<transformationCount;i++){
					array1.PutObject(GetInt(2));
					array2.PutObject(GetFloat());
					array2.PutObject(GetFloat());
					array2.PutObject(GetFloat());
				}
				
				return table;
			}
		};
		
		table.MapTable("transformationInfo", tableArrayInfoDecoder2.BuildTable(file));
		
		// rotation
		final int rotationCount = GetInt(4);
		table.MapInt("rotationCount", rotationCount);
		DecodeItem tableArrayInfoDecoder0 = new DecodeItem(){
			@Override
			public Table innterBuildTable(RandomAccessFile file)
					throws IOException {
				
				Table table = Table.createTable("rotationInfo");
				Table array1 = table.MapAndCreateArray("rotationFrameArray");
				Table array2 = table.MapAndCreateArray("transformationDataArray");
				
				if(version==11){
					for(int i=0;i<rotationCount;i++){
						array1.PutObject(GetInt(2));
						array2.PutObject(GetShort(2));
						array2.PutObject(GetShort(2));
						array2.PutObject(GetShort(2));
						array2.PutObject(GetShort(2));
					}
				}else{
					for(int i=0;i<rotationCount;i++){
						array1.PutObject(GetInt(2));
						array2.PutObject(GetFloat());
						array2.PutObject(GetFloat());
						array2.PutObject(GetFloat());
						array2.PutObject(GetFloat());
					}
				}
				return table;
			}
		};
		
		table.MapTable("rotationInfo", tableArrayInfoDecoder0.BuildTable(file));
		
		// scaling 
		final int scalingCount = GetInt(4);
		table.MapInt("scalingCount", scalingCount);
		
		DecodeItem tableArrayInfoDecoder1 = new DecodeItem(){
			@Override
			public Table innterBuildTable(RandomAccessFile file)
					throws IOException {
				
				Table table = Table.createTable("scalingInfo");
				Table array1 = table.MapAndCreateArray("scalingFrameArray");
				Table array2 = table.MapAndCreateArray("scalingDataArray");
				
				for(int i=0;i<scalingCount;i++){
					array1.PutObject(GetInt(2));
					array2.PutObject(GetFloat());
					array2.PutObject(GetFloat());
					array2.PutObject(GetFloat());
				}
				
				return table;
			}
		};
		
		table.MapTable("scalingInfo", tableArrayInfoDecoder1.BuildTable(file));
		
		return table;
	}
	
}

public class DnAniBuilder extends DecodeItem {

	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		Table dnAnim = Table.createTable("DnAnim");
		dnAnim.MapTable("header", (new DnAniHeaderDecoder()).BuildTable(file));
		dnAnim.MapTable("animateNameArray", (new DnStringVectorBuilder(dnAnim.getTable("header").getInt("animateCount"), 256)).BuildTable(file));
		dnAnim.MapTable("animateFrameArray", (new DnIntVectorBuilder(dnAnim.getTable("header").getInt("animateCount"), 4)).BuildTable(file));
		MapAndBuildArray(dnAnim, "boneInfoArray", dnAnim.getTable("header").getInt("boneCount"), 
				(new DnAniBoneInfoDecoder(dnAnim.getTable("header").getInt("animateCount"),dnAnim.getTable("header").getInt("version"))));
		
		return dnAnim;
	}

}
