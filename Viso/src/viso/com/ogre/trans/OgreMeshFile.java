package viso.com.ogre.trans;

import viso.com.table.Table;

public class OgreMeshFile {
	final Table table;
	
	private OgreMeshFile(Table table){
		this.table = table;
	}
	
	private OgreMeshFile(){
		this.table = null;
	}
	
	public Table getTable(){
		return table;
	}
	
	public static OgreMeshFile buildEmptyTable(){
		
		Table table = Table.createTable("mesh");
		
		table.MapAndCreateTable("submeshes");
		
		table.getTable("submeshes").MapAndCreateTable("submesh");
		table.getTable("submeshes").getTable("submesh").MapAndCreateTable("faces");
		table.getTable("submeshes").getTable("submesh").MapAndCreateArray("faceArray");
		
		Table geometry = table.getTable("submeshes").getTable("submesh").MapAndCreateTable("geometry");
		{
			geometry.MapAndCreateTable("vertexbuffer0");
			geometry.getTable("vertexbuffer0").MapAndCreateArray("positionArray");
			geometry.getTable("vertexbuffer0").MapAndCreateArray("normalArray");
			
			geometry.MapAndCreateTable("vertexbuffer1").MapAndCreateArray("vertexArray");
		}
		
		Table boneassignments = table.getTable("submeshes").getTable("submesh").MapAndCreateTable("boneassignments");
		{
			boneassignments.MapAndCreateArray("vertexIndexArray");
			boneassignments.MapAndCreateArray("boneIndexArray");
			boneassignments.MapAndCreateArray("weightArray");
		}
		table.getTable("submeshes").MapAndCreateTable("skeletonlink");
		
		return new OgreMeshFile(table);
	}
}
