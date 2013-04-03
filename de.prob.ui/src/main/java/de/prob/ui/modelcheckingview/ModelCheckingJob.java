package de.prob.ui.modelcheckingview;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingResult;
import de.prob.ui.Activator;

public class ModelCheckingJob extends Job {

	private final ModelChecker modelChecker;
	private boolean commandFailed;
	private ModelCheckingResult result;

	public ModelCheckingJob(final String name, final ModelChecker modelChecker) {
		super(name);
		this.modelChecker = modelChecker;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		Activator.getDefault().registerJob(this);
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		commandFailed = false;
		try {
			modelChecker.start();
			result = modelChecker.getResult();
		} catch (Exception e) {
			commandFailed = true;
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public ModelChecker getModelChecker() {
		return modelChecker;
	}

	public ModelCheckingResult getMCResult() {
		return result;
	}

	@Override
	protected void canceling() {
		if (modelChecker != null) {
			if (!modelChecker.isDone()) {
				modelChecker.cancel();
			}
		}
	}

	public boolean isCommandFailed() {
		return commandFailed;
	}

}
