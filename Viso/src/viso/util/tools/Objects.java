package viso.util.tools;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Objects {
	@SuppressWarnings( { "unchecked", "unchecked" })
	public static void checkNull(final String id, Object arg)
			throws IllegalStateException {
		if (null == arg) {
			throw new IllegalStateException("the param " + id + " is (null).");// ���������װ�� �������֤������   
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

}