package de.bmotionstudio.core.model.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.BExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.Trace;

public class BExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	private transient List<IEvalElement> formulas;
	
	@Override
	public List<IEvalElement> prepareObserver(Trace history, BControl control) {
		if (formulas == null)
			formulas = new ArrayList<IEvalElement>();
		formulas.clear();
		if (expression != null)
			formulas.add(new ClassicalB(BMotionUtil.parseFormula(expression,
					control)));
		return formulas;
	}
	
	@Override
	public void check(Trace history, BControl control,
			Map<String, EvaluationResult> results) {

		if (attribute == null || expression == null)
			return;

		EvaluationResult evalResult = results.get(BMotionUtil.parseFormula(
				expression, control));

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
