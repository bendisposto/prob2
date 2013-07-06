package de.prob.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractSession implements ISession {

	private final UUID id;

	public AbstractSession(UUID id) {
		this.id = id;
	}

	@Override
	public void command(final Map<String, String[]> parameterMap) {
		String cmd = get(parameterMap, "cmd");
		final Object delegate = this;
		Class<? extends AbstractSession> clazz = this.getClass();
		try {
			final Method method = clazz.getMethod(cmd, Map.class);
			method.invoke(delegate, parameterMap);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public abstract String html(Map<String, String[]> parameterMap);

	@Override
	public UUID getUuid() {
		return id;
	}

	public String get(Map<String, String[]> parameterMap, String key) {
		String[] strings = parameterMap.get(key);
		if (strings.length != 1)
			throw new IllegalArgumentException(
					"get Method is only applicable to simple key-Value pairs");
		return strings[0];
	}

	@Override
	public Object[] updatesSince(int lastinfo) {
		return new Object[0];
	}

}
