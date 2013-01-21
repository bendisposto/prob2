package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.History;

public class ExpressionObserver extends Observer {

	private String attribute;
	
	private Object value;
	
	private boolean expressionMode;
	
	@Override
	public void check(History history, BControl control) {
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return null;
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

	public boolean isExpressionMode() {
		return expressionMode;
	}

	public void setExpressionMode(boolean expressionMode) {
		boolean oldVal = this.expressionMode;
		this.expressionMode = expressionMode;
		firePropertyChange("expressionMode", oldVal, expressionMode);
	}

	@Override
	public String getName() {
		return "Expression Observer";
	}

}
