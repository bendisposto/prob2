package de.prob.exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.webconsole.servlets.LogServlet;

public class ProBLoggerFactory {

	private static Map<Class<?>, ProBLogger> loggers = new HashMap<Class<?>, ProBLogger>();
	private static boolean initialized = false;

	public static Logger getLogger(final Class<?> clazz) {
		ProBLogger logger = new ProBLogger(LoggerFactory.getLogger(clazz));
		loggers.put(clazz, logger);
		return logger;
	}

	public static void initialize(final LogServlet log) {
		if (!initialized) {
			Collection<ProBLogger> values = loggers.values();
			for (ProBLogger proBLogger : values) {
				proBLogger.initialize(log);
			}
		}
		initialized = true;
	}

	public static boolean isInitialized() {
		return initialized;
	}

}
