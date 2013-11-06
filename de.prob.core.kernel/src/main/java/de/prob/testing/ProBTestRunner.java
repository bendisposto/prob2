package de.prob.testing;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.google.inject.Inject;

import de.prob.scripting.ScriptEngineProvider;

public class ProBTestRunner {

	private final ScriptEngine executor;
	private final List<ProBTestListener> testListeners = new ArrayList<ProBTestListener>();
	private Class<?>[] tests = {};

	public static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;", "import spock.lang.*;",
			"import org.junit.Assert.*;" };

	@Inject
	public ProBTestRunner(final ScriptEngineProvider executorP) {
		this.executor = executorP.get();
	}

	public void runTests(final List<String> tests) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String test : tests) {
			Object translated = getTestClass(test);
			if (translated != null) {
				classes.add(translated.getClass());
			}
		}
		this.tests = new Class<?>[classes.size()];
		for (Class<?> class1 : classes) {
			this.tests[classes.indexOf(class1)] = class1;
		}
		calculateTests();
		doRun(this.tests);
	}

	private void calculateTests() {
		Request request = Request.classes(tests);
		int testNum = request.getRunner().testCount();
		for (ProBTestListener listener : testListeners) {
			listener.totalNumberOfTests(testNum);
		}
	}

	public Object getTestClass(final String test) {
		Object runScript2 = "";
		try {

			runScript2 = executor.eval(test);
		} catch (Throwable e) {
			e.printStackTrace(); // won't happen in regular mode
		}
		return runScript2;
	}

	public Result doRun(final Class<?>... classes) {
		JUnitCore jUnitCore = new JUnitCore();
		for (RunListener listener : testListeners) {
			jUnitCore.addListener(listener);
		}
		Result result = jUnitCore.run(classes);
		return result;
	}

	public void addTestListener(final ProBTestListener listener) {
		testListeners.add(listener);
	}
}
