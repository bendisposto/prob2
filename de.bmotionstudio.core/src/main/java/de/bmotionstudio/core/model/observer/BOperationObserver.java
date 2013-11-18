package de.bmotionstudio.core.model.observer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.core.editor.wizard.observer.BOperationObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;

public class BOperationObserver extends Observer {

	private String operation, attribute, predicate;

	private Object value;

	@Override
	public void check(final Trace history, final BControl control,
			final Map<String, IEvalResult> results) {

		if (operation == null || attribute == null || value == null) {
			return;
		}

		if (predicate == null || (predicate != null && predicate.length() < 1)) {

			Set<OpInfo> opList = history.getNextTransitions();
			for (OpInfo o : opList) {
				if (o.getName().equals(operation)) {
					control.setAttributeValue(attribute, value, true, false);
				}
			}

		} else {

			try {
				List<OpInfo> opFromPredicate = history
						.getStateSpace()
						.opFromPredicate(history.getCurrentState(), operation,
								BMotionUtil.parseFormula(predicate, control), 1);
				if (opFromPredicate != null && !opFromPredicate.isEmpty()) {
					control.setAttributeValue(attribute, value, true, false);
				}
			} catch (BException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public ObserverWizard getWizard(final Shell shell, final BControl control) {
		return new BOperationObserverWizard(shell, control, this);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(final String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(final String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(final String operation) {
		String oldVal = this.operation;
		this.operation = operation;
		firePropertyChange("operation", oldVal, operation);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		Object oldVal = this.value;
		this.value = value;
		firePropertyChange("value", oldVal, value);
	}

	@Override
	public String getType() {
		return "B Operation Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the value of an attribute whenever the given operation is enabled in the current state.";
	}

}
