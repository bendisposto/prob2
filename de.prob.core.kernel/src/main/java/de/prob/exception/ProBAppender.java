package de.prob.exception;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.prob.webconsole.servlets.LogServlet;

public class ProBAppender extends AppenderBase<ILoggingEvent> {

	private static LogServlet log;
	public static boolean initialized = false;

	@Override
	protected void append(final ILoggingEvent event) {
		if (initialized) {
			log.logEvent(event);
		}
	}

	public static void initialize(final LogServlet ls) {
		log = ls;
		initialized = true;
	}

}
