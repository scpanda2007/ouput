package viso.util.tools;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import viso.app.ManagedObject;

public class Objects {
	@SuppressWarnings( { "unchecked", "unchecked" })
	public static void checkNull(final String id, Object arg)
			throws IllegalStateException {
		if (null == arg) {
			throw new NullPointerException("the param " + id + " is (null).");// 将错误码封装在 具体的验证其里面   
		}
		if (arg instanceof List) {
			int count = ((List) arg).size();
			for (int i = 0; i < count; i++) {
				((List) arg).remove(null);
			}
		}
		if (arg instanceof Collection && ((Collection) arg).size() == 0) {
			throw new IllegalStateException("the param " + id + " is empty.");
		}
		if (arg instanceof Map && ((Map) arg).size() == 0) {
			throw new IllegalStateException("the param " + id + " is empty.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T uncheckedCast(Object obj){
		checkNull("uncheckedCast object",obj);
		return (T)obj;
	}

	public static String safeToString(Object object) {
		// TODO Auto-generated method stub
		return object==null? "null" : object.toString();
	}

	public static Object fastToString(ManagedObject object) {
		// TODO Auto-generated method stub
		return safeToString(object);
	}

}
