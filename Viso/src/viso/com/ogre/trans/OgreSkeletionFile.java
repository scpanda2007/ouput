package viso.com.ogre.trans;

import viso.com.table.Table;

public class OgreSkeletionFile {
	
	final Table table;
	
	private OgreSkeletionFile(Table table){
		this.table = table;
	}
	
	private OgreSkeletionFile(){
		this.table = null;
	}
	
	public Table getTable(){
		return table;
	}
	
	public static OgreSkeletionFile buildEmptyTable(){
		
		Table table = Table.createTable("skeleton");
		
		table.MapAndCreateArray("boneArray");
		table.MapAndCreateArray("boneParentArray");
		table.MapAndCreateArray("animationArray");
		
		return new OgreSkeletionFile(table);
	}
}
