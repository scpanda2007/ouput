package viso.com.crack.dn;

import java.io.IOException;
import java.io.RandomAccessFile;

import viso.com.table.DecodeItem;
import viso.com.table.Table;

public class DnShortVectorBuilder extends DecodeItem {

	private final int number;
	private final int intType;
	public DnShortVectorBuilder(int number, int intType){
		this.number = number;
		this.intType = intType;
	}
	
	@Override
	public Table innterBuildTable(RandomAccessFile file) throws IOException{
		// TODO Auto-generated method stub
		Table vector = Table.createArray("Unkown Name");
		for(int i=0;i<number;i++){
			vector.PutObject(new Integer(GetShort(intType)));
		}
		return vector;
	}

}
