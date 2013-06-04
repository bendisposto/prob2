package de.bmotionstudio.core.model.observer;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.CSPInitObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.scripting.CSPModel;
import de.prob.statespace.Trace;
import de.prob.statespace.TraceElement;
import de.prob.statespace.OpInfo;

public class CSPInitObserver extends Observer {

	private String attribute;
	
	private Object value;
	
	private Boolean isCustom;
	
	private transient static final Pattern PATTERN = Pattern.compile("\\$(.+?)\\$");

	@Override
	public void check(Trace history, BControl control,
			Map<String, EvaluationResult> results) {
		TraceElement current = history.getCurrent();
		OpInfo op = current.getOp();
		if (op == null
				|| (op != null && op.getName().equals("start_cspm_MAIN"))) {
			if (isCustom) {
				String parseExpression = parseExpression(value.toString(),
						control, history);
				CSP cspE = new CSP(parseExpression,
						(CSPModel) history.getModel());
				EvaluationResult subEval = history.evalCurrent(cspE);
				if (subEval != null && !subEval.hasError()) {
					control.setAttributeValue(attribute, subEval.value, true,
							false);
					control.getAttribute(attribute).setValue(subEval.value);
				}
			} else {
				control.getAttribute(attribute).setValue(value);
			}
		}
	}

	private String parseExpression(String expressionString,
			BControl control, Trace history) {

		String finalExpression = expressionString;
		
		OpInfo op = history.getCurrent().getOp();
		List<String> params = op.getParams();
		
		// Find expressions and collect ExpressionEvalElements
		final Matcher matcher = PATTERN.matcher(expressionString);
		while (matcher.find()) {
			int subExpr = Integer.valueOf(matcher.group(1));
			String para = params.get(subExpr);
			if (para != null)
				finalExpression = finalExpression.replace("$" + subExpr + "$",
						para);
		}
		
		return finalExpression;

	}	
	
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}
	
	public Boolean getIsCustom() {
		return isCustom;
	}
	
	public Boolean isCustom() {
		if (isCustom == null)
			isCustom = false;
		return isCustom;
	}

	public void setIsCustom(Boolean isCustom) {
		Object oldVal = this.isCustom;
		this.isCustom = isCustom;
		firePropertyChange("isCustom", oldVal, isCustom);
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
		return "CSP Init Observer";
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new CSPInitObserverWizard(shell, control, this);
	}

}
