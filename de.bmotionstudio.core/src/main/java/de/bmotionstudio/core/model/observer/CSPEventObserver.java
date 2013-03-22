package de.bmotionstudio.core.model.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.CSPEventObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.exception.ProBError;
import de.prob.scripting.CSPModel;
import de.prob.statespace.History;

public class CSPEventObserver extends Observer {

	private String expression;

	private String attribute;
	
	private Object value;
	
	private Boolean isCustom;
	
	private transient List<OpInfo> currentHisOps = new ArrayList<OpInfo>();
	
	private transient static final Pattern PATTERN = Pattern.compile("\\$(.+?)\\$");

	private transient List<String> listOfOps;
	
	private transient boolean restored;
	
	@Override
	public void check(History history, BControl control) {

		restored = false;
		
		if (currentHisOps == null)
			currentHisOps = new ArrayList<OpInfo>();

		List<OpInfo> newHisOps = history.getCurrent().getOpList();

		int diff = newHisOps.size() - currentHisOps.size();

		if (diff > 1) {
			for (int i = currentHisOps.size(); i < newHisOps.size(); i++) {
				runme(newHisOps.get(i), control, history);
			}
		} else if (diff == 1) {
			runme(history.getCurrent().getOp(), control, history);
		} else if (diff < 0) {
			if(!checkIfAlreadyRestored(control)) {
				control.restoreDefaultValue(attribute);
				restored = true;
			}
			for (int i = 0; i < newHisOps.size(); i++) {
				runme(newHisOps.get(i), control, history);
			}
		}

		currentHisOps = newHisOps;

	}
	
	private boolean checkIfAlreadyRestored(BControl control) {
		for (Observer o : control.getObservers()) {
			if (o instanceof CSPEventObserver) {
				if (((CSPEventObserver) o).isRestored())
					return true;
			}
		}
		return false;
	}

	private boolean isRestored() {
		return restored;
	}

	private void runme(OpInfo op, BControl control, History history) {

		String AsImplodedString = "";

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

		String opNameWithParameter = opName + AsImplodedString;

		if (op != null) {

			if (listOfOps == null) {

				listOfOps = new ArrayList<String>();

				String cspExpression = "bmsresult = " + expression;

				CSP cspEval = new CSP(cspExpression,
						(CSPModel) history.getModel());

				try {

					EvaluationResult eval = history.evalCurrent(cspEval);
					if (eval != null && !eval.hasError()) {

						String result = eval.value;
						result = result.replace("}", "").replace("{", "");

						String[] split = result.split(",");

						java.util.Collections.addAll(listOfOps, split);

					}

				} catch (ProBError e) {
					System.err.println(e.getMessage());
				}

			}
			
			if (listOfOps.contains(opNameWithParameter)) {
				if (isCustom) {
					String parseExpression = parseExpression(value.toString(),
							control, op);
					CSP cspE = new CSP("bmsresult=" + parseExpression,
							(CSPModel) history.getModel());
					EvaluationResult subEval = history.evalCurrent(cspE);
					if (subEval != null && !subEval.hasError()) {
						control.setAttributeValue(attribute, subEval.value,
								true, false);
					}
				} else {
					control.setAttributeValue(attribute, value, true, false);
				}
			}

		}

	}
	
	private String parseExpression(String expressionString, BControl control,
			OpInfo op) {

		String finalExpression = expressionString;

		List<String> params = op.getParams();

		if (params.size() > 0) {

			// Find expressions and collect ExpressionEvalElements
			final Matcher matcher = PATTERN.matcher(expressionString);
			while (matcher.find()) {
				int subExpr = Integer.valueOf(matcher.group(1));
				String para = params.get(subExpr);
				if (para != null)
					finalExpression = finalExpression.replace("$" + subExpr
							+ "$", para);
			}

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

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		Object oldVal = this.expression;
		this.expression = expression;
		firePropertyChange("expression", oldVal, expression);
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
		return "CSP Event Observer";
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new CSPEventObserverWizard(shell, control, this);
	}
	
}
