/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333, 26653
 *     Johan Walles: walles@mailblocks.com bug 68737
 *******************************************************************************/
package de.prob.ui.junitview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.junit.runner.notification.Failure;

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

	public void showFailure(final Failure selectedTest) {
		String trace = ""; //$NON-NLS-1$
		if (selectedTest != null) {
			trace = createTrace(selectedTest);
		}
		updateTable(trace);
	}

	private String createTrace(final Failure selectedTest) {
		StringBuilder sb = new StringBuilder();
		sb.append(selectedTest.getException().getClass().getSimpleName() + ": "
				+ selectedTest.getMessage());
		sb.append("\n");
		StackTraceElement[] stackTrace = selectedTest.getException()
				.getStackTrace();
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
