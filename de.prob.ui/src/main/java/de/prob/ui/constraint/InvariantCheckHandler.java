/**
 * 
 */
package de.prob.ui.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.animator.command.ConstraintBasedInvariantCheckCommand;
import de.prob.check.ConstraintBasedCheckingResult;
import de.prob.model.representation.BEvent;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

/**
 * 
 * @author plagge
 */
public class InvariantCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		Injector injector = ServletContextListener.INJECTOR;
		AnimationSelector selector = injector.getInstance(AnimationSelector.class);
		History currentHistory = selector.getCurrentHistory();
		Set<BEvent> events = currentHistory.getModel().getMainComponent().getChildrenOfType(BEvent.class);

		List<String> names = new ArrayList<String>();
		for (BEvent bEvent : events) {
			names.add(bEvent.getName());
		}

		performInvariantCheck(currentHistory.getStatespace(), names,  shell);
		return null;
	}

	private void performInvariantCheck(final StateSpace s, final List<String> names,
			final Shell shell) throws ExecutionException {
		if (names.isEmpty()) {
			MessageDialog.openError(shell, "Invariant Check: No Events",
					"The model does not contain any events to check!");

		} else {
			final InvariantCheckDialog dialog = new InvariantCheckDialog(shell,
					names);
			final int status = dialog.open();
			if (status == InputDialog.OK) {
				startCheck(s, dialog.getSelected(), shell);
			}
		}
	}

	private void startCheck(final StateSpace s,
			final Collection<String> events, final Shell shell)
					throws ExecutionException {
		final ConstraintBasedInvariantCheckCommand command = new ConstraintBasedInvariantCheckCommand(
				events);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				s.execute(command);
				ConstraintBasedCheckingResult result = command.getResult();

				String message = "";
				switch (result.getResult()) {
				case no_invariant_violation_found:
					message = "No Invariant Violation found.";
					break;
				case invariant_violation:
					message = "Invariant Violation found";
					break;
				case errors:
					message = "Errors occured during the execution of the command";
					break;
				case interrupted:
					message = "A time out occured or the constraint based checking was interrupted.";
					break;
				default:
					break;
				}

				final String[] buttons = new String[] { "Ok" };

				final String finalMsg = message;

				MessageDialog dialog = new MessageDialog(shell,
						"Model Checking Result", null, finalMsg,
						MessageDialog.INFORMATION, buttons, 0);

				int userAnswer = dialog.open();
			}
		});
	}

}
