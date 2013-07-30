package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

public abstract class AbstractSession implements ISession {

	private final UUID id;
	private final List<AsyncContext> clients = Collections
			.synchronizedList(new ArrayList<AsyncContext>());
	private final ArrayList<Message> responses = new ArrayList<Message>();

	public AbstractSession(UUID id) {
		this.id = id;
	}

	@Override
	public Callable<SessionResult> command(
			final Map<String, String[]> parameterMap) {
		String cmd = get(parameterMap, "cmd");
		final ISession delegate = this;
		Class<? extends AbstractSession> clazz = this.getClass();
		try {
			final Method method = clazz.getMethod(cmd, Map.class);
			return new Callable<SessionResult>() {
				@Override
				public SessionResult call() throws Exception {
					Object result = method.invoke(delegate, parameterMap);
					return new SessionResult(delegate, result);
				}
			};
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return new Callable<SessionResult>() {
			@Override
			public SessionResult call() throws Exception {
				return new SessionResult(delegate, "");
			}
		};
	}

	@Override
	public abstract String html(String clientid,
			Map<String, String[]> parameterMap);

	@Override
	public UUID getSessionUUID() {
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
	public void submit(Object result) {
		Message message = new Message(responses.size() + 1, result);
		responses.add(message);
		String json = WebUtils.toJson(message);
		synchronized (clients) {
			for (AsyncContext context : clients) {
				ServletResponse response = context.getResponse();
				try {
					PrintWriter writer = response.getWriter();
					writer.print(json);
					writer.flush();
					writer.close();
					context.complete();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					System.err.println("Late sending not succeeded: " + json);
				}
			}
			clients.clear();
		}
	}

	@Override
	public void registerClient(String client, int lastinfo, AsyncContext context) {
		if (lastinfo == 0) {

		} else {
			synchronized (clients) {
				clients.add(context);
			}
		}
	}

	@Override
	public int getResponseCount() {
		return responses.size();
	}

}
