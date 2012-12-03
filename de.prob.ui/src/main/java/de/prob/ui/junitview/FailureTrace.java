package de.prob.ui.junitview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class FailureTrace {

	private final Table fTable;

	public FailureTrace(final Composite parent, final JUnitView view) {
		fTable = new Table(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
	}

	public Composite getComposite() {
		return fTable;
	}
}
