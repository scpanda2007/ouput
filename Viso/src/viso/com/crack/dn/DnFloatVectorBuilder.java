package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;

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
