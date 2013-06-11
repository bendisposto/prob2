/**
 * 
 */
package de.prob.ui.constraint;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.animator.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.StateSpace;
import de.prob.ui.ProBCommandJob;
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
		Trace currentTrace = selector.getCurrentTrace();

		final IInputValidator validator = new PredicateValidator(currentTrace.getModel());
		final InputDialog dialog = new InputDialog(
				shell,
				"Deadlock Freedom Check",
				"ProB will search for a deadlocking state satisfying the invariant. You can (optionally) specify a predicate to constrain the search:",
				"", validator);
		final int status = dialog.open();
		if (status == InputDialog.OK) {
			startCheck(selector, currentTrace, dialog.getValue(), shell);
		}
	}

	private void startCheck(AnimationSelector selector, final Trace currentTrace, final String value,
			final Shell shell) throws ExecutionException {
		final StateSpace s = currentTrace.getStateSpace();
		final IEvalElement predicate = parsePredicate(s, value);
		final ConstraintBasedDeadlockCheckCommand command = new ConstraintBasedDeadlockCheckCommand(
				predicate);

		final Job job = new ProBCommandJob("Checking for Deadlock Freedom",
				s, command);
		job.setUser(true);
		job.addJobChangeListener(new DeadlockCheckFinishedListener(shell,selector,s));
		job.schedule();
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
			if(newText.equals("")) {
				return null;
			}

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
