package de.bmotionstudio.core.model.observer;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bmotionstudio.core.editor.wizard.observer.CSPExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.exception.ProBError;
import de.prob.scripting.CSPModel;
import de.prob.statespace.Trace;

public class CSPExpressionObserver extends Observer {

	private String attribute;

	private String expression;

	private final Logger logger = LoggerFactory
			.getLogger(CSPExpressionObserver.class);

	@Override
	public void check(final Trace history, final BControl control,
			final Map<String, IEvalResult> results) {

		if (attribute == null || expression == null) {
			return;
		}

		CSP cspEval = new CSP(expression, (CSPModel) history.getModel());

		try {

			IEvalResult evalResult = history.evalCurrent(cspEval);

			if (evalResult != null && evalResult instanceof EvalResult) {

				String result = ((EvalResult) evalResult).getValue();
				AbstractAttribute atr = control.getAttribute(attribute);
				Object unmarshalResult = atr.unmarshal(result);
				control.setAttributeValue(attribute, unmarshalResult);

			}

		} catch (ProBError e) {
			logger.error("ProBError " + e.getMessage(), e);
		}

	}

	@Override
	public ObserverWizard getWizard(final Shell shell, final BControl control) {
		return new CSPExpressionObserverWizard(shell, control, this);
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
		return "CSP Expression Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the result of the expression as the new value of the selected attribute.";
	}

}
