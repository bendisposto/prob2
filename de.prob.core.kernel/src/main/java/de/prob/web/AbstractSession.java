package de.prob.web;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.io.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.web.data.Message;
import de.prob.web.data.SessionResult;

public abstract class AbstractSession implements ISession {

	private final UUID id;
	protected final ArrayList<Message> responses = new ArrayList<Message>();

	private final Logger logger = LoggerFactory
			.getLogger(AbstractSession.class);

	public AbstractSession() {
		id = UUID.randomUUID();
	}

	public AbstractSession(final UUID id) {
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
					if (result instanceof Object[]) {
						return new SessionResult(delegate, (Object[]) result);
					} else {
						return new SessionResult(delegate, result);
					}
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

	public String get(final Map<String, String[]> parameterMap, final String key) {
		String[] strings = parameterMap.get(key);
		if (strings.length != 1) {
			throw new IllegalArgumentException(
					"get Method is only applicable to simple key-Value pairs");
		}
		return strings[0];
	}

	@Override
	public void submit(final Object... result) {
		Message message = new Message(responses.size() + 1, result);
		responses.add(message);
	}

	protected void send(final String json, final AsyncContext context) {
		ServletResponse response = context.getResponse();
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
			writer.flush();
			writer.close();
			context.complete();
		} catch (IOException e) {
			logger.error("Could not get the writer for connection.", e);
		} catch (UncheckedIOException e) {
			logger.trace("Exception occured while sending data. This happens if timeouts occured. Ignoring and continuing.");
		} catch (IllegalStateException e) {
			logger.trace("Exception occured while completing asynchronous call. This happens if timeouts occured. Ignoring and continuing.");
		}
	}

	@Override
	public void sendPendingUpdates(final String client, final int lastinfo,
			final AsyncContext context) {
		logger.trace("Register {} Lastinfo {} size {}", new Object[] { client,
				lastinfo, responses.size() });

		if (lastinfo == -1) {
			reload(client, lastinfo, context);
		} else if (lastinfo < responses.size()) {
			resend(client, lastinfo, context);
		} else {
			send("", context);
		}

	}

	protected void resend(final String client, final int lastinfo,
			final AsyncContext context) {
		Message message = responses.get(lastinfo);
		String json = WebUtils.toJson(message);
		send(json, context);
	}

	protected void resendAll(final String client, final int lastinfo,
			final AsyncContext context) {
		checkState(!responses.isEmpty(),
				"Resending is only possible if something has been sent before.");

		Message lm = responses.get(responses.size() - 1);
		ArrayList<Object> cp = new ArrayList<Object>();
		for (Message message : responses) {
			Object[] content = message.content;
			for (int i = 0; i < content.length; i++) {
				cp.add(content[i]);
			}
		}
		Object[] everything = cp.toArray();
		Message m = new Message(lm.id, everything);
		String json = WebUtils.toJson(m);
		send(json, context);
	}

	@Override
	public int getResponseCount() {
		return responses.size();
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		// Default is to not send old messages
		send("", context);
	}

	public String simpleRender(final String clientid, final String template) {
		Object scope = WebUtils.wrap("clientid", clientid);
		return WebUtils.render(template, scope);
	}

}
