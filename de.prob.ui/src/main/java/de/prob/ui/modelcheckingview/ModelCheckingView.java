package de.prob.ui.modelcheckingview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.prob.check.ConsistencyCheckingSearchOption;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

public class ModelCheckingView extends ViewPart implements
		IModelChangedListener, IHistoryChangeListener {

	private final Set<ConsistencyCheckingSearchOption> options = new HashSet<ConsistencyCheckingSearchOption>();

	private Composite container;
	private Text formulas;
	private StateSpace s;
	private ModelChecker checker;
	private History currentHistory;

	ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void createPartControl(final Composite parent) {
		final AnimationSelector selector = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
		selector.registerModelChangedListener(this);
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
		if (checker != null) {
			if (!checker.isDone()) {
				checker.cancel();
			}
		}
	}

	private void startModelChecking() {
		if (s != null) {
			checker = new ModelChecker(s, optionsToString());
			checker.start();
			finish(checker);
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

	public void finish(final ModelChecker checker) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				final Shell shell = container.getShell();

				ModelCheckingResult res = checker.getResult();

				String message = "";
				boolean traceAvailable = false;
				switch (res.getResult()) {
				case ok:
					message = "No error state found. ALL states visited.";
					break;
				case ok_not_all_nodes_considered:
					message = "No error state found. Not all states visited.";
					break;
				case deadlock:
					message = "Deadlock found";
					traceAvailable = true;
					break;
				case invariant_violation:
					message = "An invariant violation was found.";
					traceAvailable = true;
					break;
				case assertion_violation:
					message = "An assertion violation was found.";
					traceAvailable = true;
					break;
				case not_yet_finished:
					message = "Model checking was not completed successfully.";
					break;
				case state_error:
					message = "A state error occured.";
					break;
				case well_definedness_error:
					message = "A welldefinedness error occured.";
					break;
				case general_error:
					message = "An error occured";
					break;
				}

				String[] buttons = null;
				if (traceAvailable) {
					buttons = new String[] { "Ok", "Open Trace" };
				} else {
					buttons = new String[] { "Ok" };
				}

				final String finalMsg = message;
				final String[] finalButtons = buttons;

				MessageDialog dialog = new MessageDialog(shell,
						"Model Checking Result", null, finalMsg,
						MessageDialog.INFORMATION, finalButtons, 0);

				int result = dialog.open();

				if (result == 1) {
					// //This does not yet work. Working on the implementation.
					// String id =
					// OpInfo.getIdFromPrologTerm(res.getArgument(0));
					// History trace = s.getTrace(id);
					// currentHistory.notifyAnimationChange(currentHistory,
					// trace);
				}
			}
		});

	}

	@Override
	public void historyChange(final History history) {
		currentHistory = history;
	}
}
