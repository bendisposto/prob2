package de.prob.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.codehaus.groovy.tools.shell.Interpreter;

import com.google.inject.Inject;

import de.prob.webconsole.GroovyExecution;

public class ProBTestRunner {

	private GroovyExecution executor;

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;" };

	@Inject
	public ProBTestRunner(GroovyExecution executor) {
		this.executor = executor;
	}

	public void runTest(String test) {
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
			TestRunner.run((Class<? extends TestCase>) instance);
		} catch (Throwable t) {

		}
	}

}
