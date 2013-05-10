package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.BPredicateObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.History;

public class BPredicateObserver extends Observer {

	private String predicate;
	
	private String attribute;
	
	private Object value;
	
	@Override
	public void check(History history, BControl control) {

		if (predicate == null || attribute == null || value == null)
			return;

		EvaluationResult evalResult = history.evalCurrent(predicate);

		if (evalResult != null && !evalResult.hasError()) {

			String result = evalResult.value;
			Boolean bResult = Boolean.valueOf(result);
			if (bResult)
				control.setAttributeValue(attribute, value, true, false);

		}
		
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new BPredicateObserverWizard(shell, control, this);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		Object oldVal = this.value;
		this.value = value;
		firePropertyChange("value", oldVal, value);
	}

	@Override
	public String getType() {
		return "B Predicate Observer";
	}
	
	@Override
	public String getDescription() {
		return "This observer sets the value of an attribute whenever the entered predicate was evaluated to true.";
	}

}
