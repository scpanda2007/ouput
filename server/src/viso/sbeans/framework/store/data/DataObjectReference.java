package viso.sbeans.framework.store.data;

import java.io.Serializable;
import java.util.List;

import viso.sbeans.framework.service.data.DataContext;
import viso.sbeans.framework.store.DataStore;
import viso.sbeans.framework.transaction.VTransaction;

public class DataObjectReference<T extends DataObject> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private transient DataObject object;
	
	private String key;
	
	private enum State{
		New,//����һ������
		Empty,//�õ�һ�����ݿ���󣬵���ȡֵ
		UnModified,//�õ�һ�����ݿ������δ�޸�
		Dirty,//�Ѿ����޸ĵ�����
		Removed,//ɾ���ö���
		Flushed,//���޸�д�����ݿ�
		Untached//�������
	}
	
	private State state;
	
	private transient byte[] modified;//�޸ĺ�����ݶ���
	
	public DataObjectReference(DataObject object){
		this.object = object;
		state = State.New;
	}
	
	public DataObjectReference(String key){
		this.key = key;
		state = State.Empty;
	}
	
	//������û�а취��֤����
	public DataObject get(boolean lockWrite){
		switch(state){
		case Removed:
			return null;
		case New:
		case Dirty:
			return object;
		case Untached:
			throw new IllegalStateException(" This ref is not used anymore.");
		}
		state = State.UnModified;
		return this.object;
	}
	
	public void unttach(){
		state = State.Untached;
	}
	
	public void setDirty(){
		state = State.Dirty;
	}
	
	public FlushInfo flush(){
		switch(state){
		case Flushed:
			throw new IllegalStateException(" This ref is already flushed. ");
		case Untached:
			throw new IllegalStateException(" This ref is not used anymore. ");
		case New:
		case Dirty:
			return new FlushInfo(key,0,modified);
		case Removed:
			return new FlushInfo(key,1,null);
		}
		return null;
	}
	
	static public void flushAll(DataContext context,List<FlushInfo> flushes){
		VTransaction txn = context.transaction;
		DataStore store = context.store;
		for(FlushInfo flush : flushes){
			String key = flush.key;
			if(flush.type==1){
				store.delete(txn, flush.key);
				continue;
			}
			store.put(txn, key, flush.modified);
		}
	}

}
