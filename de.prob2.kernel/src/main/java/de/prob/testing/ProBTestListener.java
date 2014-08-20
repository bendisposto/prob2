package de.prob.testing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.AssertionFailedError;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.runtime.SpockComparisonFailure;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.web.views.BUnit;

@Singleton
public class ProBTestListener extends RunListener {

	private final BUnit view;
	Map<String, String> uuidMapping = new HashMap<String, String>();

	@Inject
	public ProBTestListener(final BUnit view) {
		this.view = view;
	}

	Logger logger = LoggerFactory.getLogger(ProBTestListener.class);

	@Override
	public void testRunStarted(final Description description) throws Exception {
		uuidMapping.clear();
		view.reset();
	}

	@Override
	public void testRunFinished(final Result result) throws Exception {
		view.testRunFinished(result.getRunCount(), result.getFailureCount(),
				result.getIgnoreCount());
	}

	@Override
	public void testStarted(final Description description) throws Exception {
		String randomUUID = UUID.randomUUID().toString();
		uuidMapping.put(description.getDisplayName(), randomUUID);
		view.startTest(description.getClassName(), description.getMethodName(),
				randomUUID);
	}

	@Override
	public void testFinished(final Description description) throws Exception {
		logger.info("Test Finished - " + description.getClassName() + " : "
				+ description.getMethodName() + " : "
				+ description.getDisplayName());
	}

	@Override
	public void testFailure(final Failure failure) throws Exception {
		String id = uuidMapping.get(failure.getDescription().getDisplayName());
		if (isFailure(failure)) {
			view.testFailure(
					failure.getDescription().getClassName(),
					id,
					failure.getMessage().replaceAll("\n", "<br>")
							.replaceAll(" ", "&nbsp;"));
		} else {
			view.testError(
					failure.getDescription().getClassName(),
					id,
					generateTraceMessage(failure.getMessage(),
							failure.getException()));
		}
	}

	@Override
	public void testAssumptionFailure(final Failure failure) {
		String id = uuidMapping.get(failure.getDescription().getDisplayName());
		if (isFailure(failure)) {
			view.testFailure(
					failure.getDescription().getClassName(),
					id,
					failure.getMessage().replaceAll("\n", "<br>")
							.replaceAll(" ", "&nbsp;"));
		} else {
			view.testError(
					failure.getDescription().getClassName(),
					id,
					generateTraceMessage(failure.getMessage(),
							failure.getException()));
		}
	}

	@Override
	public void testIgnored(final Description description) throws Exception {
		String randomUUID = UUID.randomUUID().toString();
		uuidMapping.put(description.getDisplayName(), randomUUID);
		view.testIgnore(description.getClassName(),
				description.getMethodName(), randomUUID);
	}

	private String generateTraceMessage(final String message, final Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>Message:</p><p>"
				+ message.replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;")
				+ "</p><p></p>");
		sb.append("<p>An Exception of " + e.getClass()
				+ " was thrown.</p><p></p><ul class='trace'>");

		for (StackTraceElement element : e.getStackTrace()) {
			if (!inFilter(element.getClassName())) {
				sb.append("<li>at " + element.getClassName() + "."
						+ element.getMethodName() + "(" + element.getFileName()
						+ ":" + element.getLineNumber() + ")</li>");
			}
		}
		sb.append("</ul>");

		return sb.toString();
	}

	private boolean inFilter(final String e) {
		if (e.startsWith("junit.framework.")) {
			return true;
		} else if (e.startsWith("java.lang.reflect.")) {
			return true;
		} else if (e.startsWith("sun.reflect.")) {
			return true;
		} else if (e.startsWith("groovy.lang.MetaClassImpl.")) {
			return true;
		} else if (e.startsWith("groovy.lang.MetaMethod.")) {
			return true;
		} else if (e.startsWith("org.codehaus.groovy.")) {
			return true;
		} else if (e.startsWith("org.junit.")) {
			return true;
		} else if (e.startsWith("junit.framework.")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if a Failure is because an assertion failed. Otherwise, an
	 * error occured. {@link AssertionFailedError}s and {@link AssertionError}s
	 * are thrown when a JUnit test fails and {@link SpockComparisonFailure}s
	 * are thrown when Spock tests fail
	 * 
	 * @param f
	 *            {@link Failure} produced by {@link JUnitCore}
	 * @return if the {@link Failure} corresponds to an actual failure of the
	 *         test or not.
	 */
	private boolean isFailure(final Failure f) {
		if (f.getException() instanceof AssertionFailedError
				|| f.getException() instanceof AssertionError
				|| f.getException() instanceof SpockComparisonFailure) {
			return true;
		}
		return false;
	}
}
