package viso.sbeans.framework.store;

public class DataObjectRefrence<T extends DataObject> {
	T object;
	public DataObjectRefrence(T obj){
		object = obj;
	}
	public T get(){
		return object;
	}
	public void set(T obj){
		object = obj;
	}
}
