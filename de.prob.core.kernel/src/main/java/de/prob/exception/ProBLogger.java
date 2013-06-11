package de.prob.exception;

import org.slf4j.Logger;
import org.slf4j.Marker;

import de.prob.webconsole.servlets.LogServlet;

public class ProBLogger implements Logger {

	private final Logger logger;
	private LogServlet log;

	public ProBLogger(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(final String msg) {
		logger.trace(msg);
	}

	@Override
	public void trace(final String format, final Object arg) {
		logger.trace(format, arg);
	}

	@Override
	public void trace(final String format, final Object arg1, final Object arg2) {
		logger.trace(format, arg1, arg2);
	}

	@Override
	public void trace(final String format, final Object[] argArray) {
		logger.trace(format, argArray);
	}

	@Override
	public void trace(final String msg, final Throwable t) {
		logger.trace(msg, t);
	}

	@Override
	public boolean isTraceEnabled(final Marker marker) {
		return logger.isTraceEnabled(marker);
	}

	@Override
	public void trace(final Marker marker, final String msg) {
		logger.trace(marker, msg);
	}

	@Override
	public void trace(final Marker marker, final String format, final Object arg) {
		logger.trace(marker, format, arg);
	}

	@Override
	public void trace(final Marker marker, final String format,
			final Object arg1, final Object arg2) {
		logger.trace(marker, format, arg1, arg2);
	}

	@Override
	public void trace(final Marker marker, final String format,
			final Object[] argArray) {
		logger.trace(marker, format, argArray);
	}

	@Override
	public void trace(final Marker marker, final String msg, final Throwable t) {
		logger.trace(marker, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(final String msg) {
		logger.debug(msg);
	}

	@Override
	public void debug(final String format, final Object arg) {
		logger.debug(format, arg);
	}

	@Override
	public void debug(final String format, final Object arg1, final Object arg2) {
		logger.debug(format, arg1, arg2);
	}

	@Override
	public void debug(final String format, final Object[] argArray) {
		logger.debug(format, argArray);
	}

	@Override
	public void debug(final String msg, final Throwable t) {
		logger.debug(msg, t);
	}

	@Override
	public boolean isDebugEnabled(final Marker marker) {
		return logger.isDebugEnabled(marker);
	}

	@Override
	public void debug(final Marker marker, final String msg) {
		logger.debug(marker, msg);
	}

	@Override
	public void debug(final Marker marker, final String format, final Object arg) {
		logger.debug(marker, format, arg);
	}

	@Override
	public void debug(final Marker marker, final String format,
			final Object arg1, final Object arg2) {
		logger.debug(marker, format, arg1, arg2);
	}

	@Override
	public void debug(final Marker marker, final String format,
			final Object[] argArray) {
		logger.debug(marker, format, argArray);
	}

	@Override
	public void debug(final Marker marker, final String msg, final Throwable t) {
		logger.debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(final String msg) {
		logger.info(msg);
	}

	@Override
	public void info(final String format, final Object arg) {
		logger.info(format, arg);
	}

	@Override
	public void info(final String format, final Object arg1, final Object arg2) {
		logger.info(format, arg1, arg2);
	}

	@Override
	public void info(final String format, final Object[] argArray) {
		logger.info(format, argArray);
	}

	@Override
	public void info(final String msg, final Throwable t) {
		logger.info(msg, t);
	}

	@Override
	public boolean isInfoEnabled(final Marker marker) {
		return logger.isInfoEnabled(marker);
	}

	@Override
	public void info(final Marker marker, final String msg) {
		logger.info(marker, msg);
	}

	@Override
	public void info(final Marker marker, final String format, final Object arg) {
		logger.info(marker, format, arg);

	}

	@Override
	public void info(final Marker marker, final String format,
			final Object arg1, final Object arg2) {
		logger.info(marker, format, arg1, arg2);
	}

	@Override
	public void info(final Marker marker, final String format,
			final Object[] argArray) {
		logger.info(marker, format, argArray);
	}

	@Override
	public void info(final Marker marker, final String msg, final Throwable t) {
		logger.info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(final String msg) {
		logger.warn(msg);
	}

	@Override
	public void warn(final String format, final Object arg) {
		logger.warn(format, arg);
	}

	@Override
	public void warn(final String format, final Object[] argArray) {
		logger.warn(format, argArray);
	}

	@Override
	public void warn(final String format, final Object arg1, final Object arg2) {
		logger.warn(format, arg1, arg2);
	}

	@Override
	public void warn(final String msg, final Throwable t) {
		logger.warn(msg, t);
	}

	@Override
	public boolean isWarnEnabled(final Marker marker) {
		return logger.isWarnEnabled(marker);
	}

	@Override
	public void warn(final Marker marker, final String msg) {
		logger.warn(marker, msg);
	}

	@Override
	public void warn(final Marker marker, final String format, final Object arg) {
		logger.warn(marker, format, arg);
	}

	@Override
	public void warn(final Marker marker, final String format,
			final Object arg1, final Object arg2) {
		logger.warn(marker, format, arg1, arg2);
	}

	@Override
	public void warn(final Marker marker, final String format,
			final Object[] argArray) {
		logger.warn(marker, format, argArray);
	}

	@Override
	public void warn(final Marker marker, final String msg, final Throwable t) {
		logger.warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(final String msg) {
		logger.error(msg);
	}

	@Override
	public void error(final String format, final Object arg) {
		logger.error(format, arg);
	}

	@Override
	public void error(final String format, final Object arg1, final Object arg2) {
		logger.error(format, arg1, arg2);
	}

	@Override
	public void error(final String format, final Object[] argArray) {
		logger.error(format, argArray);
	}

	@Override
	public void error(final String msg, final Throwable t) {
		logger.error(msg, t);
	}

	@Override
	public boolean isErrorEnabled(final Marker marker) {
		return logger.isErrorEnabled(marker);
	}

	@Override
	public void error(final Marker marker, final String msg) {
		logger.error(marker, msg);
	}

	@Override
	public void error(final Marker marker, final String format, final Object arg) {
		logger.error(marker, format, arg);
	}

	@Override
	public void error(final Marker marker, final String format,
			final Object arg1, final Object arg2) {
		logger.error(marker, format, arg1, arg2);
	}

	@Override
	public void error(final Marker marker, final String format,
			final Object[] argArray) {
		logger.error(format, argArray);
	}

	@Override
	public void error(final Marker marker, final String msg, final Throwable t) {
		logger.error(marker, msg, t);
	}

	public void initialize(final LogServlet log) {
		if (this.log == null) {
			this.log = log;
		}
	}

}
