package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.ExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.History;

public class ExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	@Override
	public void check(History history, BControl control) {
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new ExpressionObserverWizard(shell, control, this);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		Object oldVal = this.expression;
		this.expression = expression;
		firePropertyChange("expression", oldVal, expression);
	}

	@Override
	public String getType() {
		return "Expression Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the result of the expression as the new value of the selected attribute.";
	}

}
