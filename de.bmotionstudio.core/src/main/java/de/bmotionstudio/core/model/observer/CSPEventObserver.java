package de.bmotionstudio.core.model.observer;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.CSPEventObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ExpressionObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.scripting.CSPModel;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;
import de.prob.statespace.StateId;

public class CSPEventObserver extends Observer {

	private String expression;

	private String attribute;
	
	private Object value;
	
	@Override
	public void check(History history, BControl control) {

		String AsImplodedString = "";

		HistoryElement current = history.getCurrent();
		OpInfo op = current.getOp();
		String opName = op.getName();
		List<String> opParameter = op.getParams();
		
		if (opParameter.size() > 0) {

			String[] inputArray = opParameter.toArray(new String[opParameter
					.size()]);

			StringBuffer sb = new StringBuffer();
			sb.append(inputArray[0]);
			for (int i = 1; i < inputArray.length; i++) {
				sb.append(".");
				sb.append(inputArray[i]);
			}
			AsImplodedString = "." + sb.toString();

		}
		
//		String cspExpression = "bmsresult = member(" + opName + AsImplodedString + ","
//				+ expression + ")";
		String cspExpression = "zz = {1}";
		
		System.out.println("===> " + cspExpression);
		
//		
		CSP cspEval = new CSP(cspExpression, (CSPModel) history.getModel());
		EvaluationResult eval = history.eval(cspEval);
		
		System.out.println("RESULT: " + eval.getValue());

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
		return "CSP Event Observer";
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new CSPEventObserverWizard(shell, control, this);
	}

}
