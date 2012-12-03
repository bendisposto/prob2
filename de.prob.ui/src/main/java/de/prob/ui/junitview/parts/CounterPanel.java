/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.prob.ui.junitview.parts;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.prob.ui.Activator;

/**
 * A panel with counters for the number of Runs, Errors and Failures.
 */
public class CounterPanel extends Composite {
	protected Text fNumberOfErrors;
	protected Text fNumberOfFailures;
	protected Text fNumberOfRuns;
	protected int fTotal;
	protected int fIgnoredCount;

	private final Image fErrorIcon = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_ERROR_OVR).createImage();
	private final Image fFailureIcon = Activator.getDefault()
			.getImageRegistry().getDescriptor(Activator.JUNIT_FAILED_OVR)
			.createImage();

	public CounterPanel(final Composite parent) {
		super(parent, SWT.WRAP);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 9;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		fNumberOfRuns = createLabel("Runs:", null, " 0/0  "); //$NON-NLS-1$
		fNumberOfErrors = createLabel("Errors:", fErrorIcon, " 0 "); //$NON-NLS-1$
		fNumberOfFailures = createLabel("Failures:", fFailureIcon, " 0 "); //$NON-NLS-1$

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeIcons();
			}
		});
	}

	private void disposeIcons() {
		fErrorIcon.dispose();
		fFailureIcon.dispose();
	}

	private Text createLabel(final String name, final Image image,
			final String init) {
		Label label = new Label(this, SWT.NONE);
		if (image != null) {
			image.setBackground(label.getBackground());
			label.setImage(image);
		}
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		label = new Label(this, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		// label.setFont(JFaceResources.getBannerFont());

		Text value = new Text(this, SWT.READ_ONLY);
		value.setText(init);
		// bug: 39661 Junit test counters do not repaint correctly [JUnit]
		value.setBackground(getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_BEGINNING));
		return value;
	}

	public void reset() {
		setErrorValue(0);
		setFailureValue(0);
		setRunValue(0, 0);
		fTotal = 0;
	}

	public void setTotal(final int value) {
		fTotal = value;
	}

	public int getTotal() {
		return fTotal;
	}

	public void setRunValue(final int value, final int ignoredCount) {
		String runString;
		if (ignoredCount == 0) {
			runString = MessageFormat.format(
					"{1}/{2}",
					new Object[] { Integer.toString(value),
							Integer.toString(fTotal) });
		} else {
			runString = MessageFormat.format(
					"{1}/{2} ({3} ignored)",
					new Object[] { Integer.toString(value),
							Integer.toString(fTotal),
							Integer.toString(ignoredCount) });
		}
		fNumberOfRuns.setText(runString);

		if (fIgnoredCount == 0 && ignoredCount > 0 || fIgnoredCount != 0
				&& ignoredCount == 0) {
			layout();
		} else {
			fNumberOfRuns.redraw();
			redraw();
		}
		fIgnoredCount = ignoredCount;
	}

	public void setErrorValue(final int value) {
		fNumberOfErrors.setText(Integer.toString(value));
		redraw();
	}

	public void setFailureValue(final int value) {
		fNumberOfFailures.setText(Integer.toString(value));
		redraw();
	}
}
