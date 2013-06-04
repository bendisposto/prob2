package de.prob.ui.modelcheckingview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.prob.check.ConsistencyCheckingSearchOption;
import de.prob.check.ModelChecker;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

public class ModelCheckingView extends ViewPart implements
IModelChangedListener, IAnimationChangeListener {

	private final Set<ConsistencyCheckingSearchOption> options = new HashSet<ConsistencyCheckingSearchOption>();

	private Composite container;
	private Text formulas;
	private StateSpace s;
	private Trace currentTrace;
	private Job job;


	@Override
	public void createPartControl(final Composite parent) {
		final AnimationSelector selector = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
		selector.registerModelChangedListener(this);
		selector.registerAnimationChangeListener(this);
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		setSettings(container);

		Label textLabel = new Label(container, SWT.NONE);
		textLabel.setText("Add Further Formulas:");
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		formulas = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		formulas.setText("");
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		formulas.setLayoutData(gd);

		final Button start = new Button(container, SWT.PUSH);
		gd = new GridData(SWT.CENTER, SWT.FILL, false, false);
		start.setLayoutData(gd);
		start.setText("Start");
		MCSelectionListener listener = new MCSelectionListener();
		start.addSelectionListener(listener);

		Button cancel = new Button(container, SWT.PUSH);
		gd = new GridData(SWT.CENTER, SWT.FILL, false, false);
		cancel.setLayoutData(gd);
		cancel.setText("Cancel");
		cancel.addSelectionListener(listener);
	}

	private class MCSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			Object source = e.getSource();
			if (source instanceof Button) {
				String x = ((Button) source).getText();
				if (x.equals("Start")) {
					startModelChecking();
				} else if (x.equals("Cancel")) {
					cancelModelChecking();
				}
			}
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}
	}

	private void cancelModelChecking() {
		if(job != null) {
			job.cancel();
		}
	}

	private void startModelChecking() {
		if (s != null) {
			job = new ModelCheckingJob("Consistency Checking",new ModelChecker(s, optionsToString()));
			job.setUser(true);
			job.addJobChangeListener(new ConsistencyCheckingFinishedListener(container,currentTrace));
			job.schedule();
		}
	}

	private void setSettings(final Composite container) {
		Group settings = new Group(container, SWT.NONE);
		settings.setText("Settings");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		settings.setLayoutData(gridData);
		settings.setLayout(new RowLayout(SWT.VERTICAL));

		for (int i = 0; i < 5; i++) {
			final Button button = new Button(settings, SWT.CHECK);
			final ConsistencyCheckingSearchOption option = ConsistencyCheckingSearchOption
					.get(i);
			button.setText(option.getDescription());
			button.setSelection(option.isEnabledByDefault());
			setOptions(button.getSelection(), option);
			button.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					setOptions(button.getSelection(), option);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
				}
			});
		}
	}

	private void setOptions(final boolean set,
			final ConsistencyCheckingSearchOption option) {
		if (set) {
			if (!options.contains(option)) {
				options.add(option);
			}
		} else {
			if (options.contains(option)) {
				options.remove(option);
			}
		}
	}

	private List<String> optionsToString() {
		List<String> list = new ArrayList<String>();
		for (ConsistencyCheckingSearchOption option : options) {
			list.add(option.name());
		}
		return list;
	}

	@Override
	public void setFocus() {
		container.setFocus();
	}

	private void resetChecker(final StateSpace s) {
		this.s = s;
	}

	@Override
	public void modelChanged(final StateSpace s) {
		resetChecker(s);
	}

	@Override
	public void traceChange(final Trace trace) {
		currentTrace = trace;
	}
}
