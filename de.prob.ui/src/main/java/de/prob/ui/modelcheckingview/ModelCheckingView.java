package de.prob.ui.modelcheckingview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;

public class ModelCheckingView extends ViewPart {

	private boolean breadth_first = false;
	private boolean deadlock = true;
	private boolean invariant = true;
	private boolean theorem = false;
	private boolean errors = false;

	private Composite container;

	@Override
	public void createPartControl(final Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		setSettings(container);
		final Button start = new Button(container, SWT.PUSH);
		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, false, false);
		start.setLayoutData(gridData);
		start.setText("Start Consistency Checking");
		start.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				startModelChecking();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
	}

	private void startModelChecking() {
		// TODO Auto-generated method stub

	}

	private void setSettings(final Composite container) {
		Group settings = new Group(container, SWT.NONE);
		settings.setText("Settings");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		settings.setLayoutData(gridData);
		settings.setLayout(new RowLayout(SWT.VERTICAL));

		final Button bf_button = new Button(settings, SWT.CHECK);
		bf_button.setText("Breadth First Search");
		bf_button.setSelection(breadth_first);
		bf_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				breadth_first = bf_button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});

		final Button dead_button = new Button(settings, SWT.CHECK);
		dead_button.setText("Find Deadlocks");
		dead_button.setSelection(deadlock);
		dead_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				deadlock = dead_button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});

		final Button inv_button = new Button(settings, SWT.CHECK);
		inv_button.setText("Find Invariant Violations");
		inv_button.setSelection(invariant);
		inv_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				invariant = inv_button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});

		final Button th_button = new Button(settings, SWT.CHECK);
		th_button.setText("Find Theorem Violations");
		th_button.setSelection(theorem);
		th_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				theorem = th_button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});

		final Button er_button = new Button(settings, SWT.CHECK);
		er_button.setText("Search for New Errors");
		er_button.setSelection(errors);
		er_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				errors = er_button.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
	}

	@Override
	public void setFocus() {
		container.setFocus();
	}

}
