/**
 * 
 */
package de.prob.ui.constraint;

import java.util.concurrent.Callable;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.animator.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.check.ConstraintBasedCheckingResult;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;


public class DeadlockCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		performDeadlockCheck(shell);
		return null;
	}

	private void performDeadlockCheck(final Shell shell)
			throws ExecutionException {
		Injector injector = ServletContextListener.INJECTOR;
		AnimationSelector selector = injector.getInstance(AnimationSelector.class);
		History currentHistory = selector.getCurrentHistory();

		final IInputValidator validator = new PredicateValidator(currentHistory.getModel());
		final InputDialog dialog = new InputDialog(
				shell,
				"Deadlock Freedom Check",
				"ProB will search for a deadlocking state satisfying the invariant. You can (optionally) specify a predicate to constrain the search:",
				"", validator);
		final int status = dialog.open();
		if (status == InputDialog.OK) {
			startCheck(currentHistory, dialog.getValue(), shell);
		}
	}

	private void startCheck(final History currentHistory, final String value,
			final Shell shell) throws ExecutionException {
		final StateSpace s = currentHistory.getS();
		final IEvalElement predicate = parsePredicate(s, value);
		final ConstraintBasedDeadlockCheckCommand command = new ConstraintBasedDeadlockCheckCommand(
				predicate);
		Callable<ConstraintBasedCheckingResult> toCall = new Callable<ConstraintBasedCheckingResult>() {

			@Override
			public ConstraintBasedCheckingResult call() throws Exception {
				s.execute(command);
				return command.getResult();
			}
		};

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				s.execute(command);
				ConstraintBasedCheckingResult result = command.getResult();

				String message = "";
				boolean traceAvailable = false;
				switch (result.getResult()) {
				case no_deadlock_found:
					message = "No deadlock found.";
					break;
				case deadlock:
					message = "Deadlock found";
					traceAvailable = true;
					break;
				case errors:
					message = "Errors occured during the execution of the command";
					break;
				case interrupted:
					message = "The execution of the command was interrupted";
					break;
				default:
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

				int userAnswer = dialog.open();

				if (userAnswer == 1) {
					String id = command.getDeadlockStateId();
					History trace = s.getTrace(id);
					currentHistory.notifyAnimationChange(currentHistory, trace);
				}
			}
		});
	}

	private IEvalElement parsePredicate(final StateSpace s,
			final String input) throws ExecutionException {

		AbstractModel model = s.getModel();
		IEvalElement predicate = null;
		if (input != null && input.trim().isEmpty()) {
			predicate = null;
		} else {
			try {
				if(model instanceof EventBModel) {
					predicate = new EventB(input);
				} else if(model instanceof ClassicalBModel) {
					predicate = new ClassicalB(input);
				}
			} catch (Exception e) {
				throw (new ExecutionException(
						"Exception while parsing the input", e));
			}
		}
		return predicate;
	}

	private static final class PredicateValidator implements IInputValidator {

		private final AbstractModel model;
		private IEvalElement pred;

		public PredicateValidator(final AbstractModel model) {
			this.model = model;
		}

		@Override
		public String isValid(final String newText) {
			if(model instanceof EventBModel) {
				try {
					pred = new EventB(newText);
				} catch(EvaluationException e) {
					return "Could not parse EventB formula";
				}
				if(!pred.getKind().equals("#PREDICATE")) {
					return "EventB formula is not a predicate";
				}
			}
			if(model instanceof ClassicalBModel) {
				try {
					pred = new ClassicalB(newText);
				} catch(EvaluationException e) {
					return "Could not parse ClassicalB formula";
				}
				if(!pred.getKind().equals("#PREDICATE")) {
					return "ClassicalB formula is not a predicate";
				}

			}
			return null;
		}
	}
}
