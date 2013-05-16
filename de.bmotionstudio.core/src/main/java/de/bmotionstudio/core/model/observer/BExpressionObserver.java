package de.bmotionstudio.core.model.observer;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.BExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.History;

public class BExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	@Override
	public void check(History history, BControl control) {

		if (attribute == null || expression == null)
			return;

		EvaluationResult evalResult = history.evalCurrent(BMotionUtil
				.parseFormula(expression, control));

		if (evalResult != null && !evalResult.hasError()) {

			String result = evalResult.value;
			AbstractAttribute atr = control.getAttribute(attribute);
			Object unmarshalResult = atr.unmarshal(result);
			control.setAttributeValue(attribute, unmarshalResult, true, false);

		}

	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new BExpressionObserverWizard(shell, control, this);
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
		return "B Expression Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the result of the expression as the new value of the selected attribute.";
	}

}
