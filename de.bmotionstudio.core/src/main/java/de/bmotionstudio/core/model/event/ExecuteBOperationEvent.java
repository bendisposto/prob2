package de.bmotionstudio.core.model.event;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.core.editor.wizard.event.BExecuteOperationWizard;
import de.bmotionstudio.core.editor.wizard.event.EventWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class ExecuteBOperationEvent extends Event {

	private final AnimationSelector animations = ServletContextListener.INJECTOR
			.getInstance(AnimationSelector.class);
	private String operation;

	private String predicate;

	@Override
	public void execute(final Trace history, final BControl control) {

		if (operation == null) {
			return;
		}

		try {

			String fpredicate = predicate;

			if (fpredicate == null
					|| (fpredicate != null && fpredicate.length() < 1)) {
				fpredicate = "1=1";
			}

			Trace newHistory = history.add(operation,
					BMotionUtil.parseFormula(fpredicate, control));
			animations.replaceTrace(history, newHistory);

		} catch (BException e1) {
		} catch (IllegalArgumentException e2) {
		}

	}

	@Override
	public EventWizard getWizard(final Shell shell, final BControl control) {
		return new BExecuteOperationWizard(shell, control, this);
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(final String operation) {
		String oldVal = this.operation;
		this.operation = operation;
		firePropertyChange("operation", oldVal, operation);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(final String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}

	@Override
	public String getType() {
		return "Execute B Operation";
	}

	@Override
	public String getTooltipText(final Trace history, final BControl control) {
		String p = (predicate == null) ? "" : BMotionUtil.parseFormula(
				predicate, control);
		return "Execute B Operation: " + operation + "(" + p + ") ("
				+ checkIfOperationEnabled(history, control) + ")";
	}

	private boolean checkIfOperationEnabled(final Trace history,
			final BControl control) {

		if (predicate == null || (predicate != null && predicate.length() < 1)) {

			Set<OpInfo> opList = history.getNextTransitions();
			for (OpInfo o : opList) {
				if (o.getName().equals(operation)) {
					return true;
				}
			}

		} else {

			try {
				List<OpInfo> opFromPredicate = history
						.getStateSpace()
						.opFromPredicate(history.getCurrentState(), operation,
								BMotionUtil.parseFormula(predicate, control), 1);
				if (opFromPredicate != null && !opFromPredicate.isEmpty()) {
					return true;
				}
			} catch (BException e) {
				e.printStackTrace();
			}

		}

		return false;

	}

}
