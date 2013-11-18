package viso.test.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Utilities for using reflection in tests. */
public class UtilReflection {

	/** Returns the specified class. */
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}

	/** Returns the specified declared constructor, making it accessible. */
	public static <T> Constructor<T> getConstructor(Class<T> cl,
			Class<?>... params) {
		try {
			Constructor<T> result = cl.getDeclaredConstructor(params);
			result.setAccessible(true);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}

	/** Returns the specified declared field, making it accessible. */
	public static Field getField(Class<?> cl, String fieldName) {
		try {
			Field result = cl.getDeclaredField(fieldName);
			result.setAccessible(true);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}

	/** Returns the specified declared method, making it accessible. */
	public static Method getMethod(Class<?> cl, String methodName,
			Class<?>... params) {
		try {
			Method result = cl.getDeclaredMethod(methodName, params);
			result.setAccessible(true);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception: " + e, e);
		}
	}
}
