package de.prob.web.views;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;

import com.google.inject.Singleton;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class BUnit extends AbstractSession {

	public BUnit() {
		incrementalUpdate = false;
	}

	Set<String> suites = new HashSet<String>();

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/bunit/index.html");
	}

	public void reset() {
		submit(WebUtils.wrap("cmd", "BUnit.clearInput"));
		suites.clear();
	}

	public void startTest(final String suiteName, final String testName,
			final String testId) {
		if (!suites.contains(suiteName)) {
			submit(WebUtils.wrap("cmd", "BUnit.addSuite", "name", suiteName));
			suites.add(suiteName);
		}
		submit(WebUtils.wrap("cmd", "BUnit.addTest", "suite", suiteName,
				"name", testName, "id", testId));
	}

	public void testFailure(final String suiteName, final String testId,
			final String message) {
		submit(WebUtils.wrap("cmd", "BUnit.testFail", "suite", suiteName,
				"test", testId, "reason", message));
	}

	public void testError(final String suiteName, final String testId,
			final String message) {
		submit(WebUtils.wrap("cmd", "BUnit.testError", "suite", suiteName,
				"test", testId, "reason", message));
	}

	public void testIgnore(final String suiteName, final String testName,
			final String testId) {
		if (!suites.contains(suiteName)) {
			submit(WebUtils.wrap("cmd", "BUnit.addSuite", "name", suiteName));
			suites.add(suiteName);
		}
		submit(WebUtils.wrap("cmd", "BUnit.testIgnore", "suite", suiteName,
				"name", testName, "id", testId));
	}

	public void testRunFinished(final int runCount, final int failCount,
			final int ignoreCount) {
		submit(WebUtils.wrap("cmd", "BUnit.setStats", "success", runCount
				- failCount, "total", runCount, "ignored", ignoreCount));
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		reset();
	}
}
