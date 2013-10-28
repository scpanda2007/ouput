package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;

class DnStringVectorBuilder extends DecodeItem {
	
	private int number = 0;
	private int size = 0;
	public DnStringVectorBuilder(int number, int size){
		this.number = number;
		this.size = size;
	}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException{
		// TODO Auto-generated method stub
		Table vector = Table.createArray("Unkown Name");
		for(int i=0;i<number;i++)
			vector.PutObject(GetString(size));
		return vector;
	}
	
}

