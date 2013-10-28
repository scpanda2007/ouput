package viso.com.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	
	private Map<String,Object> dictionary;
	private List<Object> array;
	private String name;
	
	private Table(String name){ this.name = name; }
	private Table(){}
	private Table(final Table table){}
	
	public void setName(String name){
		this.name = name;
	}
	
	public static Table createTable(String name){
		Table table = new Table(name);
		table.dictionary = new HashMap<String,Object>();
		return table;
	}
	
	public static Table createArray(String name){
		Table table = new Table(name);
		table.array = new ArrayList<Object>();
		return table;
	}
	
	boolean find(String name){
		return dictionary.containsKey(name);
	}
	
	public List<Object> toArray(){
		if(array==null){
			throw new UnsupportedOperationException();
		}
		return array;
	}
	
	public void PutObject(Object object){
		array.add(object);
	}
	
	public Table MapAndCreateArray(String name){
		Table table = Table.createArray(name);
		dictionary.put(name, table);
		return table;
	}
	
	public Table MapAndBuildArray(String name,DecodeItem decoder,RandomAccessFile file,int repeat) throws IOException{
		Table table = MapAndCreateArray(name);
		for(int i=0;i<repeat;i++){
			table.PutObject(decoder.BuildTable(file));
		}
		return table;
	}
	
	public Table PutAndCreateArray(String name){
		Table table = Table.createArray(name);
		array.add(table);
		return table;
	}
	
	public void MapTable(String name, Table table){
		dictionary.put(name, table);
	}
	
	public void MapInt(String name, int value){
		dictionary.put(name, new Integer(value));
	}
	
	public void MapString(String name, String value){
		dictionary.put(name, value);
	}
	
	public void MapFloat(String name, float value){
		dictionary.put(name, new Float(value));
	}
	
	public int getInt(String name){
		if(dictionary==null){
			throw new UnsupportedOperationException();
		}
		Object value = dictionary.get(name);
		if(value == null){
			throw new NullPointerException();
		}
		if(value instanceof Integer){
			return ((Integer)value).intValue();
		}else{
			throw new UnsupportedOperationException();
		}
	}
	
	public float getFloat(String name){
		if(dictionary==null){
			throw new UnsupportedOperationException();
		}
		Object value = dictionary.get(name);
		if(value == null){
			throw new NullPointerException();
		}
		if(value instanceof Float){
			return ((Float)value).floatValue();
		}else{
			throw new UnsupportedOperationException();
		}
	}
	
	public String getString(String name){
		if(dictionary==null){
			throw new UnsupportedOperationException();
		}
		Object value = dictionary.get(name);
		if(value == null){
			return null;
		}
		if(value instanceof String){
			return ((String)value);
		}else{
			throw new UnsupportedOperationException();
		}
	}
	
	public Table getTable(String name){
		if(dictionary==null){
			throw new UnsupportedOperationException();
		}
		Object value = dictionary.get(name);
		if(value == null){
			return null;
		}
		if(value instanceof Table){
			return ((Table)value);
		}else{
			throw new UnsupportedOperationException();
		}
	}
}
