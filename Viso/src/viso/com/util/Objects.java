package viso.com.util;

public class Objects {
	
	/**
	 * 检测一个对象是否为空，如果为空则抛出异常
	 * @param name 对象名
	 * @param obj 需要检测的对象
	 * */
	public static void checkNull(String name, Object obj){
		if(obj==null){
			throw new NullPointerException(" Object : "+name+" is null.");
		}
	}
}
