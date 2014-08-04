package de.prob.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.apache.commons.lang.StringEscapeUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;

import com.google.inject.Singleton;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class Log extends AbstractSession {

	private volatile List<LogElement> elements = new ArrayList<LogElement>();

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/log/index.html");
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Log.addEntries",
				"entries", WebUtils.toJson(elements));
		submit(wrap);
	}

	public synchronized void logEvent(final ILoggingEvent event) {
		String from = event.getLoggerName();
		if (event.hasCallerData()) {
			StackTraceElement[] callerData = event.getCallerData();
			if (callerData.length > 0) {
				StackTraceElement call = callerData[0];
				from = call.getClassName() + "." + call.getMethodName() + ":"
						+ call.getLineNumber();
			}
		}
		String level = event.getLevel().toString().toLowerCase();
		if (!level.equals("trace")) {
			LogElement entry = new LogElement(from, level,
					event.getFormattedMessage());
			elements.add(entry);
			Map<String, String> wrap = WebUtils.wrap("cmd", "Log.addEntry",
					"entry", WebUtils.toJson(entry));
			submit(wrap);
		}
	}

	class LogElement {
		public final String type;
		public final String msg;
		public final String from;
		public final String level;

		public LogElement(final String from, final String level,
				final String msg) {
			this.from = from;
			type = level;
			this.msg = StringEscapeUtils.escapeHtml(msg);
			this.level = level.toString();
		}
	}

}
