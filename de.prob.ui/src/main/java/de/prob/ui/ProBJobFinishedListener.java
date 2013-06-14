package de.prob.ui;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;

public abstract class ProBJobFinishedListener extends JobChangeAdapter {

	public ProBJobFinishedListener() {
		super();
	}

	@Override
	public void done(final IJobChangeEvent event) {
		super.done(event);
		Job job = event.getJob();
		if (job instanceof ProBCommandJob) {
			final ProBCommandJob checkJob = (ProBCommandJob) job;
			if (!checkJob.isCommandFailed()) {
				showResult(checkJob.getCommand(), checkJob.getAnimator());
			}
		} else {
			final String message = "The job has a wrong type. Expected ProBCommandJob but got "
					+ job.getClass();
			//Logger.notifyUserWithoutBugreport(message);
		}
	}

	abstract protected void showResult(AbstractCommand command,
			IAnimator animator);
}