package viso.com.util;

public class Objects {
	
	/**
	 * ���һ�������Ƿ�Ϊ�գ����Ϊ�����׳��쳣
	 * @param name ������
	 * @param obj ��Ҫ���Ķ���
	 * */
	public static void checkNull(String name, Object obj){
		if(obj==null){
			throw new NullPointerException(" Object : "+name+" is null.");
		}
	}
}
