/**
 * 
 */
package de.prob.ui.constraint;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.check.ConstraintBasedCheckingResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.ProBJobFinishedListener;

/**
 * This JobChangeAdapter presents the user the results of a deadlock freedom
 * check.
 * 
 * @see DeadlockCheckHandler
 * 
 * @author plagge
 */
public class DeadlockCheckFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;
	private AnimationSelector selector;
	private StateSpace statespace;

	public DeadlockCheckFinishedListener(final Shell shell, AnimationSelector selector, StateSpace s) {
		this.shell = shell;
		this.selector = selector;
		this.statespace = s;
	}

	@Override
	protected void showResult(final AbstractCommand cmd,
			final IAnimator animator) {
		final ConstraintBasedDeadlockCheckCommand command = (ConstraintBasedDeadlockCheckCommand) cmd;
		final ConstraintBasedCheckingResult result = command.getResult();
		final int dialogType;
		final String dialogTitle;
		final String message;
		if (result == null) {
			dialogType = MessageDialog.ERROR;
			dialogTitle = "Errow During Deadlock Freedom Check";
			message = "ProB did not return a result";
		} else {
			switch (result.getResult()) {
			case no_deadlock_found:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "No Deadlock Found";
				message = "The model does not contain any deadlock.";
				break;
			case errors:
				dialogType = MessageDialog.ERROR;
				dialogTitle = "Errow During Deadlock Freedom Check";
				message = "An unexpected error occurred while typechecking the given predicate.";
				break;
			case deadlock:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "DEADLOCK FOUND!";
				message = "The model contains a deadlocking state satisfying the invariant, it will be shown in the state view.";

				Trace traceToDeadlock = statespace.getTrace(command.getDeadlockStateId());
				selector.replaceTrace(selector.getCurrentTrace(), traceToDeadlock);
				break;
			case interrupted:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "User Interrupt";
				message = "The deadlock check has been interrupted by the user or a time-out.";
				break;
			default:
				// Logger.notifyUser("Unexpected result: " + result);
				return;
			}
		}
		if (shell.isDisposed()) {
			System.out.println("Deadlock freedom check finished: "
					+ dialogTitle);
		} else {
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					MessageDialog.open(dialogType, shell, dialogTitle, message,
							SWT.NONE);
				}
			};
			shell.getDisplay().asyncExec(runnable);
		}
	}

}
