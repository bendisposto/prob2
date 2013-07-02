package de.prob.web;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class AbstractSession implements ISession {

	private UUID id;
	private SessionQueue queue;

	@Override
	public Callable<Object> requestJson(final Map<String, String[]> parameterMap) {
		String cmd = get(parameterMap, "cmd");
		final Object delegate = this;
		Class<? extends AbstractSession> clazz = this.getClass();
		try {
			final Method method = clazz.getMethod(cmd, Map.class);
			return new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return method.invoke(delegate, parameterMap);
				}
			};
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public abstract String requestHtml(Map<String, String[]> parameterMap);

	@Override
	public void setUuid(UUID id) {
		if (this.id != null)
			throw new IllegalStateException("Cannot set UUID multiple times");
		this.id = id;
	}

	@Override
	public UUID getUuid() {
		return id;
	}

	@Override
	public void setQueue(SessionQueue queue) {
		if (this.queue != null)
			throw new IllegalStateException("Cannot set Queue multiple times");
		this.queue = queue;
	}

	@Override
	public SessionQueue getQueue() {
		return queue;
	}

	public String get(Map<String, String[]> parameterMap, String key) {
		String[] strings = parameterMap.get(key);
		if (strings.length != 1)
			throw new IllegalArgumentException(
					"get Method is only applicable to simple key-Value pairs");
		return strings[0];
	}

}
