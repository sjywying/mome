package com.mome.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mome.core.exception.FieldNotFoundException;

/**
 * 反射工具方法
 * 
 * @author Yang XuePing
 */
public final class ReflectionUtils {
	private static final Log log = LogFactory.getLog(ReflectionUtils.class);

	public static Field getDeclaredField(Class baseClass, String fieldName) {
		for (Class<?> superClass = baseClass; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
		}
		return null;
	}

	public static Field getDeclaredField(Object object, String fieldName) {
		return getDeclaredField(object.getClass(), fieldName);
	}

	protected static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers())
				|| !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new FieldNotFoundException("不存在属性：" + field);
		}

		return getAndWrap(field, object);
	}

	public static void setFieldValue(final Object object,
			final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("不存在属性：" + field);
		}

		setAndWrap(field, object, value);
	}

	public static Object invoke(Method method, Object target, Object... args)
			throws Exception {
		try {
			return method.invoke(target, args);
		} catch (IllegalArgumentException iae) {
			String message = "Could not invoke method by reflection: "
					+ toString(method);
			if (args != null && args.length > 0) {
				message += " with parameters: ("
						+ Strings.toClassNameString(", ", args) + ')';
			}
			message += " on: " + target.getClass().getName();
			throw new IllegalArgumentException(message, iae);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof Exception) {
				throw (Exception) ite.getCause();
			} else {
				throw ite;
			}
		}
	}

	public static Object get(Field field, Object target) throws Exception {
		boolean accessible = field.isAccessible();
		try {
			field.setAccessible(true);
			return field.get(target);
		} catch (IllegalArgumentException iae) {
			String message = "Could not get field value by reflection: "
					+ toString(field) + " on: " + target.getClass().getName();
			throw new IllegalArgumentException(message, iae);
		} finally {
			field.setAccessible(accessible);
		}
	}

	public static void set(Field field, Object target, Object value)
			throws Exception {
		try {
			field.set(target, value);
		} catch (IllegalArgumentException iae) {
			// target may be null if field is static so use
			// field.getDeclaringClass() instead
			String message = "Could not set field value by reflection: "
					+ toString(field) + " on: "
					+ field.getDeclaringClass().getName();
			if (value == null) {
				message += " with null value";
			} else {
				message += " with value: " + value.getClass();
			}
			throw new IllegalArgumentException(message, iae);
		}
	}

	public static Object getAndWrap(Field field, Object target) {
		boolean accessible = field.isAccessible();
		try {
			field.setAccessible(true);
			return get(field, target);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new IllegalArgumentException("exception setting: "
						+ field.getName(), e);
			}
		} finally {
			field.setAccessible(accessible);
		}
	}

	public static void setAndWrap(Field field, Object target, Object value) {
		boolean accessible = field.isAccessible();
		try {
			field.setAccessible(true);
			set(field, target, value);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new IllegalArgumentException("exception setting: "
						+ field.getName(), e);
			}
		} finally {
			field.setAccessible(accessible);
		}
	}

	public static Object invokeAndWrap(Method method, Object target,
			Object... args) {
		try {
			return invoke(method, target, args);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException("exception invoking: "
						+ method.getName(), e);
			}
		}
	}

	public static Object invokeAndWrap(String methodName,
			Class<?>[] parameterTypes, Object target, Object... args) {
		try {
			Method method = target.getClass().getMethod(methodName,
					parameterTypes);
			return invoke(method, target, args);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException("exception invoking: "
						+ methodName, e);
			}
		}
	}

	public static String toString(Method method) {
		return Strings.unqualify(method.getDeclaringClass().getName()) + '.'
				+ method.getName() + '('
				+ Strings.toString(", ", method.getParameterTypes()) + ')';
	}

	public static String toString(Member member) {
		return Strings.unqualify(member.getDeclaringClass().getName()) + '.'
				+ member.getName();
	}

	/**
	 * 根据一个标记了@Entity的实体类，找到其上层的类
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?> getRootEntityClass(Class<?> clazz) {

		if (!clazz.isAnnotationPresent(Entity.class))
			return clazz;

		Class<?> superClass = clazz.getSuperclass();

		while (!superClass.equals(Object.class)) {
			if (superClass.isAnnotationPresent(Entity.class)
					|| superClass.isAnnotationPresent(MappedSuperclass.class)) {
				superClass = superClass.getSuperclass();
				continue;
			} else
				break;
		}
		return superClass;
	}
}
