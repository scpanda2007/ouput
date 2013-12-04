package viso.com.util;

public class Objects {
	public static void checkNull(String param, Object obj){
		if(obj==null){
			throw new NullPointerException(param+" should not be (null)");
		}
	}
}
