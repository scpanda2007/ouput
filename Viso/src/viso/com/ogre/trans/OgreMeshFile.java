package viso.com.ogre.trans;

import viso.com.table.Table;

public class OgreMeshFile {
	final Table table;
	
	private OgreMeshFile(){
		table = Table.createTable("mesh");
	}
	
	public void buildDefaultTable(){
		table.MapAndCreateTable("submeshes");
		table.getTable("submeshes").MapAndCreateTable("submesh");
		table.getTable("submeshes").getTable("submesh").MapAndCreateTable("faces");
		table.getTable("submeshes").getTable("submesh").MapAndCreateArray("faceArray");
		table.getTable("submeshes").getTable("submesh").MapAndCreateTable("geometry");
		Table boneassignments = table.getTable("submeshes").getTable("submesh").MapAndCreateTable("boneassignments");
		{
			boneassignments.MapAndCreateArray("vertexIndexArray");
			boneassignments.MapAndCreateArray("boneIndexArray");
			boneassignments.MapAndCreateArray("weightArray");
		}
		table.getTable("submeshes").MapAndCreateTable("skeletonlink");
	}
}
