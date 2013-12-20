package viso.sbeans.framework.store.data;

public class FlushInfo {
	public byte[] modified;
	public int type;//0ÐÞ¸Ä 1É¾³ý
	public String key;
	public FlushInfo(String key, int type, byte[] modified){
		this.key = key;
		this.type = type;
		this.modified = modified;
	}
}
