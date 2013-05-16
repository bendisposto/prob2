package de.bmotionstudio.core.model.event;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.core.editor.wizard.event.BExecuteOperationWizard;
import de.bmotionstudio.core.editor.wizard.event.EventWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.statespace.History;
import de.prob.statespace.OpInfo;

public class ExecuteBOperationEvent extends Event {

	private String operation;
	
	private String predicate;
	
	@Override
	public void execute(History history, BControl control) {

		if(operation == null)
			return;
		
		try {

			String fpredicate = predicate;

			if (fpredicate == null
					|| (fpredicate != null && fpredicate.length() < 1))
				fpredicate = "1=1";

			History newHistory = history.add(operation,
					BMotionUtil.parseFormula(fpredicate, control));
			newHistory.notifyAnimationChange(history, newHistory);

		} catch (BException e1) {
		} catch (IllegalArgumentException e2) {
		}
		
	}
	
	@Override
	public EventWizard getWizard(Shell shell, BControl control) {
		return new BExecuteOperationWizard(shell, control, this);
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		String oldVal = this.operation;
		this.operation = operation;
		firePropertyChange("operation", oldVal, operation);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}
	
	@Override
	public String getType() {
		return "Execute B Operation";
	}
	
	@Override
	public String getTooltipText(History history, BControl control) {
		String p = (predicate == null) ? "" : BMotionUtil.parseFormula(
				predicate, control);
		return "Execute B Operation: " + operation + "(" + p + ") ("
				+ checkIfOperationEnabled(history, control) + ")";
	}
	
	private boolean checkIfOperationEnabled(History history, BControl control) {

		if (predicate == null || (predicate != null && predicate.length() < 1)) {

			Set<OpInfo> opList = history.getNextTransitions();
			for (OpInfo o : opList) {
				if (o.getName().equals(operation))
					return true;
			}

		} else {

			try {
				List<OpInfo> opFromPredicate = history
						.getStatespace()
						.opFromPredicate(history.getCurrentState(), operation,
								BMotionUtil.parseFormula(predicate, control), 1);
				if (opFromPredicate != null && !opFromPredicate.isEmpty())
					return true;
			} catch (BException e) {
				e.printStackTrace();
			}

		}

		return false;

	}

}
