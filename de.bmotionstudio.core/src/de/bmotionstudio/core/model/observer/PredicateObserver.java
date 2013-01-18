package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.Animation;
import de.bmotionstudio.core.model.control.BControl;

public class PredicateObserver extends Observer {

	private String predicate;
	
	private String attribute;
	
	private Object value;
	
	@Override
	public void check(Animation animation, BControl control) {
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return null;
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
