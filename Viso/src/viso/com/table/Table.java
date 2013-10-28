package viso.com.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Table {
	
	private Map<String,Object> dictionary;
	public final Map<String, Object> distinctElements(){
		return dictionary;
	}
		
	private List<Object> array;
	public final List<Object> repeatElements(){
		return array;
	}

	@SuppressWarnings("unused")
	private String name;
	
	private Table(String name){this.name = name;}
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
	
	public Table MapAndBuildArray(String name,DecodeItem decoder,RandomAccessFile file,int repeat) throws IOException{
		Table table = MapAndCreateArray(name);
		for(int i=0;i<repeat;i++){
			table.PutObject(decoder.BuildTable(file));
		}
		return table;
	}
	
	public Table MapAndCreateTable(String name){
		Table table = Table.createTable(name);
		dictionary.put(name, table);
		return table;
	}
	
	public Table MapAndCreateArray(String name){
		Table table = Table.createArray(name);
		dictionary.put(name, table);
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
	
	public static class PrefixTabStr{
		public static int counter = 0;
		public static String prefix = "";
		public static String Prefix(){
			return prefix;
		}
		public static void push(){
			counter++;
			prefix+="\t";
		}
		public static void pop(){
			counter--;
			if(counter<0){
				counter=0;
				return;
			}
			prefix = prefix.substring(0,prefix.length()-1);
		}
		public static void clear(){
			prefix = "";
			counter = 0;
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[\n");
		PrefixTabStr.push();
		if(this.dictionary!=null){
			Iterator<Entry<String, Object>> iter = dictionary.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, Object> entry = iter.next();
				buffer.append(PrefixTabStr.Prefix()+entry.getKey()+": ");
				Object obj = entry.getValue();
				if(obj==null){
					buffer.append("(null)\n");
					continue;
				}
				if(obj instanceof Table){
					buffer.append(((Table)obj).toString()+"\n");
					continue;
				}
				if(obj instanceof Float || 
						obj instanceof String ||
						obj instanceof Integer){
					buffer.append(obj.toString()+"\n");
					continue;
				}
				throw new IllegalStateException(" unkown value of :"+entry.getKey());
			}
		}else if(this.array != null){
			buffer.append(PrefixTabStr.Prefix()+"size : "+this.array.size()+" \n");
			for(Object obj : this.array){
				if(obj==null){
					buffer.append(PrefixTabStr.Prefix()+"(null)\n");
					continue;
				}
				if(obj instanceof Table){
					buffer.append(PrefixTabStr.Prefix()+((Table)obj).toString()+"\n");
					continue;
				}
				if(obj instanceof Float || 
						obj instanceof String ||
						obj instanceof Integer){
					buffer.append(PrefixTabStr.Prefix()+obj.toString()+"\n");
					continue;
				}
				throw new IllegalStateException(" unkown value of :"+obj.toString());
			}
		}else{
			buffer.append(PrefixTabStr.Prefix()+"( empty )\n");
		}
		PrefixTabStr.pop();
		buffer.append(PrefixTabStr.Prefix()+"]");
		return buffer.toString();
	}
}
