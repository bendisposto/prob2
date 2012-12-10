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
	private final List<IProBTestListener> testListeners = new ArrayList<IProBTestListener>();
	private List<TestSuite> tests = new ArrayList<TestSuite>();

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
		this.tests = new ArrayList<TestSuite>();
		for (String test : tests) {
			TestSuite translated = translateTest(test);
			if (translated != null) {
				this.tests.add(translated);
			}
		}
		calculateTests();
		for (TestSuite test : this.tests) {
			doRun(test);
		}
	}

	private void calculateTests() {
		int testNum = 0;
		for (TestSuite test : tests) {
			testNum += test.countTestCases();
		}
		for (IProBTestListener listener : testListeners) {
			listener.totalNumberOfTests(testNum);
		}
	}

	public TestSuite translateTest(final String test) {
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
			return new TestSuite(instance.getClass());
		} catch (Throwable t) {
			System.out.println("Test: " + test
					+ " is not of valid form and therefore was ignored.");
		}
		return null;
	}

	public TestResult doRun(final Test suite) {
		TestResult result = new TestResult();
		for (TestListener listener : testListeners) {
			result.addListener(listener);
		}
		suite.run(result);
		return result;
	}

	public void addTestListener(final IProBTestListener listener) {
		testListeners.add(listener);
	}
}
