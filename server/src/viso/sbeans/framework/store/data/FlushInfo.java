package viso.sbeans.framework.store.data;

public class FlushInfo {
	public byte[] modified;
	public int type;//0�޸� 1ɾ��
	public String key;
	public FlushInfo(String key, int type, byte[] modified){
		this.key = key;
		this.type = type;
		this.modified = modified;
	}
}
