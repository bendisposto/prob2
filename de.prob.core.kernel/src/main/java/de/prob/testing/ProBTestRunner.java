package de.prob.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.codehaus.groovy.tools.shell.Interpreter;

import com.google.inject.Inject;

import de.prob.webconsole.GroovyExecution;

public class ProBTestRunner {

	private final GroovyExecution executor;
	private final List<TestListener> testListeners = new ArrayList<TestListener>();

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;" };

	@Inject
	public ProBTestRunner(final GroovyExecution executor) {
		this.executor = executor;
	}

	public void runTests(final List<String> tests) {
		for (String test : tests) {
			runTest(test);
		}
	}

	@SuppressWarnings("unchecked")
	public void runTest(final String test) {
		final Interpreter tinterpreter = new Interpreter(this.getClass()
				.getClassLoader(), executor.getBindings());

		assert test != null;
		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(Arrays.asList(IMPORTS));
		eval.addAll(Collections.singletonList(test));
		try {
			// this executes the test script resulting in a loaded class
			// the last line of the test script must return an instance of the
			// test
			Object instance = tinterpreter.evaluate(eval);
			doRun(new TestSuite(instance.getClass()));
		} catch (Throwable t) {
			System.out.println("Test: " + test
					+ " is not of valid form and therefore was ignored.");
		}
	}

	public TestResult doRun(final Test suite) {
		TestResult result = new TestResult();
		for (TestListener listener : testListeners) {
			result.addListener(listener);
		}
		suite.run(result);
		return result;
	}

	public void addTestListener(final TestListener listener) {
		testListeners.add(listener);
	}
}
