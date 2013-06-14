package de.prob.ui.modelcheckingview;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.prob.check.ModelCheckingResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class ConsistencyCheckingFinishedListener extends JobChangeAdapter {

	private final Composite container;
	private final Trace currentTrace;
	private final AnimationSelector animations;

	public ConsistencyCheckingFinishedListener(final Composite container,
			final Trace currentTrace) {
		this.container = container;
		this.currentTrace = currentTrace;
		animations = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
	}

	@Override
	public void done(final IJobChangeEvent event) {
		super.done(event);
		Job job = event.getJob();
		if (job instanceof ModelCheckingJob) {
			final ModelCheckingJob checkJob = (ModelCheckingJob) job;
			if (!checkJob.isCommandFailed()) {
				showResult(checkJob.getMCResult());
			}
		} else {
			final String message = "The job has a wrong type. Expected ProBCommandJob but got "
					+ job.getClass();
			// Logger.notifyUserWithoutBugreport(message);
		}
	}

	private void showResult(final ModelCheckingResult res) {

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

		Runnable openResultWindow = new Runnable() {
			@Override
			public void run() {
				final Shell shell = container.getShell();
				MessageDialog dialog = new MessageDialog(shell,
						"Model Checking Result", null, finalMsg,
						MessageDialog.INFORMATION, finalButtons, 0);

				int result = dialog.open();

				if (result == 1) {
					String id = OpInfo.getIdFromPrologTerm(res.getArgument(0));
					Trace trace = currentTrace.getStateSpace().getTrace(id);
					animations.replaceTrace(currentTrace, trace);
				}
			}
		};

		Display.getDefault().asyncExec(openResultWindow);

	}

}
