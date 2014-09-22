package de.prob.web.worksheet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import de.prob.web.WebUtils;

public final class VariableDetailTransformer implements
		Function<Map.Entry<String, Object>, Map<String, String>> {

	private static final Set<String> processed = new HashSet<String>();

	public static void clear() {
		processed.clear();
	}

	// Will be used in JS
	@SuppressWarnings("unused")
	private class CMethod {

		public final boolean isStatic;
		public final String name;
		public final String returntype;
		public final boolean isAccessible;
		public final Collection<String> parameterTypes;
		public final String declaringClass;
		public final Collection<String> exceptions;

		public CMethod(Method m, boolean isStatic) {
			this.isStatic = isStatic;
			this.name = m.getName();
			this.returntype = m.getReturnType().getSimpleName();
			this.isAccessible = m.isAccessible();
			this.declaringClass = m.getDeclaringClass().getName();
			List<Class<?>> exc = Arrays.asList(m.getExceptionTypes());
			this.exceptions = Collections2.transform(exc,
					classToSimpleNameTransformer);

			List<Class<?>> ptc = Arrays.asList(m.getParameterTypes());
			this.parameterTypes = Collections2.transform(ptc,
					classToSimpleNameTransformer);
		}

		private final Function<Class<?>, String> classToSimpleNameTransformer = new Function<Class<?>, String>() {
			@Override
			public String apply(Class<?> input) {
				return input.getSimpleName();
			}
		};
	}

	private final BindingsSnapshot previous_snapshot;
	private final BindingsSnapshot current_snapshot;

	// Will be used in JS
	@SuppressWarnings("unused")
	private static class CAttribute {
		public final String name;
		public final String type;
		public final boolean isAccessible;
		public final boolean isStatic;

		public CAttribute(Field f, boolean isStatic) {
			this.isStatic = isStatic;
			this.name = f.getName();
			this.type = f.getType().getSimpleName();
			this.isAccessible = f.isAccessible();
		}
	}

	public VariableDetailTransformer(BindingsSnapshot previous_snapshot,
			BindingsSnapshot current_snapshot) {
		this.previous_snapshot = previous_snapshot;
		this.current_snapshot = current_snapshot;
	}

	@Override
	public Map<String, String> apply(Entry<String, Object> input) {
		Object value = input.getValue();
		Class<? extends Object> clazz = value.getClass();
		String className = clazz.getName();

		HashMap<String, String> result = new HashMap<String, String>();
		result.put("name", input.getKey());
		result.put("value", value.toString());
		result.put("clazz", className);
		result.put("supertype", clazz.getSuperclass().getName());
		if (!processed.contains(className)) {
			result.put("attributes", getAttributes(clazz));
			result.put("methods", getMethods(clazz));
		}
		result.put("fresh", String.valueOf(current_snapshot.delta(
				previous_snapshot).contains(input.getKey())));
		processed.add(className);
		return result;
	}

	private String getMethods(Class<? extends Object> clazz) {
		ArrayList<CMethod> result = new ArrayList<CMethod>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			result.add(new CMethod(method, false));
		}
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			result.add(new CMethod(method, true));
		}
		return WebUtils.toJson(result);
	}

	private String getAttributes(Class<? extends Object> clazz) {
		ArrayList<CAttribute> result = new ArrayList<CAttribute>();
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			result.add(new CAttribute(field, true));
		}
		Field[] fields = clazz.getFields();

		for (Field field : fields) {
			result.add(new CAttribute(field, false));
		}
		return WebUtils.toJson(result);
	}

}