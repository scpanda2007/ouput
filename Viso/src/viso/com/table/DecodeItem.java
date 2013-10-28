package viso.com.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class DecodeItem {
	
	private TableDecoder decode;
	private static TableDecoder defaultDecoder = new TableDecoder();
	
	public static void setDefaultDecoder(TableDecoder _defaultDecoder){
		defaultDecoder = _defaultDecoder; 
	}
	
	public DecodeItem(){}
	
	public DecodeItem(TableDecoder decode){
		this.decode = decode;
	}
	
	private TableDecoder getDecoder(){
		if(this.decode!=null)return this.decode;
		return defaultDecoder;
	}
	
	protected Table tmp;
	private RandomAccessFile file;
	
	protected int GetInt(int number) throws IOException{
		return getDecoder().DecodeInt(file, number);
	}
	
	protected String GetString(int number) throws IOException{
		return getDecoder().DecodeString(file, number);
	}
	
	protected int GetInt() throws IOException{
		return getDecoder().DecodeInt(file, 4);
	}
	
	protected float GetFloat() throws IOException{
		return getDecoder().DecodeFloat(file, 4);
	}
	
	protected float GetFloat(int number) throws IOException{
		return getDecoder().DecodeFloat(file, number);
	}
	
	public Table BuildTable(RandomAccessFile file) throws IOException{
		this.file = file;
		return innterBuildTable(file);
	}
	
	public abstract Table innterBuildTable(RandomAccessFile file) throws IOException;
}
