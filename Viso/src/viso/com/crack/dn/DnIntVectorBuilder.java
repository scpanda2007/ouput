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
