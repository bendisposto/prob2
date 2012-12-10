package de.prob.ui.junitview;

import junit.framework.TestFailure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class FailureTrace {
	private static final int MAX_LABEL_LENGTH = 256;
	static final String FRAME_PREFIX = "at ";

	private final Table fTable;
	private final FailureTableDisplay fFailureTableDisplay;
	private final String[] filter = { "org.eclipse.*", "com.google.inject.*",
			"sun.reflect.*", "de.prob.testing.*", "de.prob.junit.*",
			"org.codehaus.groovy.*", "javax.servlet.*",
			"groovysh_evaluate$run.call(null:-1)", "de.prob.webconsole.*",
			"groovy.lang.MetaClassImpl.*", "java.lang.reflect.*",
			"junit.framework.*", "java.lang.Thread.*",
			"de.prob.ui.junitview.*", "groovy.lang.MetaMethod.*",
			"groovy.lang.Closure.*" };

	public FailureTrace(final Composite parent, final JUnitView view) {
		fTable = new Table(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);

		fFailureTableDisplay = new FailureTableDisplay(fTable);
	}

	public Composite getComposite() {
		return fTable;
	}

	public void showFailure(final TestFailure test) {
		String trace = ""; //$NON-NLS-1$
		if (test != null) {
			trace = createTrace(test);
		}
		updateTable(trace);
	}

	private String createTrace(final TestFailure test) {
		StringBuilder sb = new StringBuilder();
		sb.append(test.thrownException().getClass().getSimpleName() + ": "
				+ test.exceptionMessage());
		sb.append("\n");
		StackTraceElement[] stackTrace = test.thrownException().getStackTrace();
		for (StackTraceElement e : stackTrace) {
			sb.append(" at " + e.getClassName() + "." + e.getMethodName() + "("
					+ e.getFileName() + ":" + e.getLineNumber() + ")\n");
		}
		return sb.toString();
	}

	private void updateTable(String trace) {
		if (trace == null || trace.trim().equals("")) { //$NON-NLS-1$
			clear();
			return;
		}
		trace = trace.trim();
		fTable.setRedraw(false);
		fTable.removeAll();
		new TextualTrace(trace, filter).display(fFailureTableDisplay,
				MAX_LABEL_LENGTH);
		fTable.setRedraw(true);
	}

	public void clear() {
		fTable.removeAll();
	}
}
