package de.bmotionstudio.core.model.observer;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.CSPExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.exception.ProBError;
import de.prob.scripting.CSPModel;
import de.prob.statespace.Trace;

public class CSPExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	@Override
	public void check(Trace history, BControl control,
			Map<String, EvaluationResult> results) {

		if (attribute == null || expression == null)
			return;

		CSP cspEval = new CSP(expression,
				(CSPModel) history.getModel());

		try {

			EvaluationResult evalResult = history.evalCurrent(cspEval);

			if (evalResult != null && !evalResult.hasError()) {

				String result = evalResult.value;
				AbstractAttribute atr = control.getAttribute(attribute);
				Object unmarshalResult = atr.unmarshal(result);
				control.setAttributeValue(attribute, unmarshalResult);

			}

		} catch (ProBError e) {
			System.err.println(e.getMessage());
		}

	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new CSPExpressionObserverWizard(shell, control, this);
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
		return "CSP Expression Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the result of the expression as the new value of the selected attribute.";
	}

}
