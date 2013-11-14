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
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.Trace;

public class BExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	private transient List<IEvalElement> formulas;

	@Override
	public List<IEvalElement> prepareObserver(final Trace history,
			final BControl control) {
		if (formulas == null) {
			formulas = new ArrayList<IEvalElement>();
		}
		formulas.clear();
		if (expression != null) {
			formulas.add(new ClassicalB(BMotionUtil.parseFormula(expression,
					control)));
		}
		return formulas;
	}

	@Override
	public void check(final Trace history, final BControl control,
			final Map<String, IEvalResult> results) {

		if (attribute == null || expression == null) {
			return;
		}

		IEvalResult evalResult = results.get(BMotionUtil.parseFormula(
				expression, control));

		if (evalResult != null && evalResult instanceof EvalResult) {
			String result = ((EvalResult) evalResult).getValue();
			AbstractAttribute atr = control.getAttribute(attribute);
			Object unmarshalResult = atr.unmarshal(result);
			control.setAttributeValue(attribute, unmarshalResult, true, false);
		}

	}

	@Override
	public ObserverWizard getWizard(final Shell shell, final BControl control) {
		return new BExpressionObserverWizard(shell, control, this);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(final String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(final String expression) {
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
