package viso.com.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Element;


class DefaultTableXmlEncoder extends TableXmlEncoder{
	
	@Override
	protected Element innerPrintSelfTo() {
		// TODO Auto-generated method stub
		defaultPrintAttributes();
		return defaultPrintChildren();
	}
	
}

/**
 * 文件生成
 * */
public abstract class TableXmlEncoder {
	
	protected Element node;
	protected Table table;
	
	protected static Map<String, TableXmlEncoder> encoders = new HashMap<String, TableXmlEncoder>();
	
	public static void register(String type, TableXmlEncoder encoder){
		if(encoders.containsKey(type)) throw new IllegalStateException("已经存在一个名为<"+type+">的实例了");
		encoders.put(type, encoder);
	}
	
	static interface PrintMethod{
		public Element PrintObjectTo(Element node, String key, Object obj);
	}
	
	protected static boolean TableXmlEncoderInitOnce = false;
	public static void registerAll(){
		if(TableXmlEncoderInitOnce)return;
		encoders.put("default", new DefaultTableXmlEncoder());
	}
	
	public static Element print(String encoder, Element tonode, Table totable){
		if(!encoders.containsKey(encoder)){
			throw new IllegalStateException(" the encoder class : <"+encoder+"> can not be found. ");
		}
		return encoders.get(encoder).print(tonode, totable);
	}
	
	/**
	 * 将table中的元素打印到一个xml元素上
	 * */
	public Element print(Element node, Table table){
		this.node = node;
		this.table = table;
		if(this.table != null) return innerPrintSelfTo();
		return node;
	}
	
	/**
	 * 将基础值以属性方式打印到xml文件的元素上
	 * */
	protected Element defaultPrintAttributes() {
		return trivalPrint(node, table, new PrintMethod() {
			@Override
			public Element PrintObjectTo(final Element tonode, final String key, final Object obj) {
				if (obj instanceof Integer || obj instanceof Float
						|| obj instanceof String) {
					tonode.addAttribute(key, obj.toString());
				}
				return tonode;
			}
		},null);
	}
	
	protected Element defaultPrintChildren() {
		return trivalPrint(node, table, new PrintMethod() {
			@Override
			public Element PrintObjectTo(final Element tonode, final String key, final Object obj) {
				if (obj instanceof Table) {
					final Element child = tonode.addElement(key);
					print("default", child, table);
				}
				return tonode;
			}
		},null);
	}
	
	protected Element printChild(String decoder, String attribute, String name){
		return print(decoder, appendChild(attribute), table.getTable(name));
	}
	
	protected Element printChild(String decoder, String attribute){
		return printChild(decoder, attribute, attribute);
	}
	
	protected Element printChild(String attribute){
		return printChild("default", attribute);
	}
	
	protected static Element appendChildTo(Element tonode, String key){
		return tonode.addElement(key);
	}
	
	protected Element appendChild(String key){
		return appendChildTo(node, key);
	}
	
	protected Element addAttributes(String key, Object obj){
		addArributesTo(node, key, obj);
		return node;
	}
	
	protected static Element addArributesTo(Element tonode, String key, Object obj){
		if (obj instanceof Integer || obj instanceof Float
				|| obj instanceof String) {
			tonode.addAttribute(key, obj.toString());
		}
		return tonode;
	}
	
	protected Element trivalPrint(Element node, Table table, PrintMethod method, Set<String> excepts){
		
		Map<String, Object> attributes = table.distinctElements();
		Iterator<Entry<String, Object>> iter = attributes.entrySet().iterator();
		Object obj;
		String key;
		while(iter.hasNext()){
			
			Entry<String, Object> entry = iter.next();
			obj = entry.getValue();
			key = entry.getKey();
			
			if(excepts != null && excepts.contains(key)) continue;
			
			if(obj==null){
				continue;
			}
			if(obj instanceof Table){
				continue;
			}
			
			method.PrintObjectTo(node, key, obj);
		}
		return node;
	}
	
	protected abstract Element innerPrintSelfTo();
	
}
