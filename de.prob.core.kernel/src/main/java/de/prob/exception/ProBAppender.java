package de.prob.exception;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.prob.web.views.Log;

public class ProBAppender extends AppenderBase<ILoggingEvent> {

	private static Log log;
	public static boolean initialized = false;

	@Override
	protected void append(final ILoggingEvent event) {
		if (initialized) {
			log.logEvent(event);
		}
	}

	public static void initialize(final Log log2) {
		log = log2;
		initialized = true;
	}

}
