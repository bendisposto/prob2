/**
 * 
 */
package de.prob.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;

/**
 * This jobs takes a command as argument and executes its during the run. If the
 * user selects cancel, the animator is asked to send an user interruption
 * signal to the Prolog core.
 * 
 * @author plagge
 */
public class ProBCommandJob extends Job {
	private final IAnimator animator;
	private final AbstractCommand command;

	private boolean commandFailed = false;

	public ProBCommandJob(final String name, final IAnimator animator,
			final AbstractCommand command) {
		super(name);
		this.animator = animator;
		this.command = command;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		Activator.getDefault().registerJob(this);
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		commandFailed = false;
		try {
			animator.execute(command);
		} catch (Exception e) {
			commandFailed = true;
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	@Override
	protected void canceling() {
		animator.sendInterrupt();
	}

	public AbstractCommand getCommand() {
		return command;
	}

	public IAnimator getAnimator() {
		return animator;
	}

	public boolean isCommandFailed() {
		return commandFailed;
	}

}
