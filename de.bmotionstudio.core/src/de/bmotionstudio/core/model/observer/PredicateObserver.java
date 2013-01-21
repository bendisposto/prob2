package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.PredicateObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.History;

public class PredicateObserver extends Observer {

	private String predicate;
	
	private String attribute;
	
	private Object value;
	
	@Override
	public void check(History history, BControl control) {
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new PredicateObserverWizard(shell, control, this);
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
	public String getName() {
		return "Predicate Observer";
	}

}
