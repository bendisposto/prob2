package de.bmotionstudio.core.model.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bmotionstudio.core.editor.wizard.observer.CSPEventObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.exception.ProBError;
import de.prob.scripting.CSPModel;
import de.prob.statespace.Trace;
import de.prob.statespace.OpInfo;

public class CSPEventObserver extends Observer {

	private String expression;

	private String attribute;

	private Object value;

	private Boolean isCustom;

	private transient List<OpInfo> currentHisOps = new ArrayList<OpInfo>();

	private transient static final Pattern PATTERN = Pattern
			.compile("\\$(.+?)\\$");

	private transient List<String> listOfOps;

	private transient int setPosition = 0;

	private transient boolean restored = false;

	private final Logger logger = LoggerFactory
			.getLogger(CSPEventObserver.class);

	@Override
	public void check(Trace history, BControl control,
			Map<String, EvaluationResult> results) {

		if (currentHisOps == null)
			currentHisOps = new ArrayList<OpInfo>();

		List<OpInfo> newHisOps = history.getCurrent().getOpList();

		int diff = newHisOps.size() - currentHisOps.size();

		if (diff > 1) {
			for (int i = currentHisOps.size(); i < newHisOps.size(); i++) {
				runme(newHisOps.get(i), i, control, history);
			}
		} else if (diff < 0) {
			if (!checkIfAlreadyRestored(control)) {
				control.restoreDefaultValue(attribute);
				restored = true;
			}
			for (int i = 0; i < newHisOps.size(); i++) {
				runme(newHisOps.get(i), i, control, history);
			}
		} else {
			runme(history.getCurrent().getOp(), -1, control, history);
		}

		currentHisOps = newHisOps;

	}

	@Override
	public void afterCheck(Trace history, BControl control) {
		setPosition = 0;
		restored = false;
	}

	private boolean checkIfAlreadyRestored(BControl control) {
		for (Observer o : control.getObservers()) {
			if (o instanceof CSPEventObserver) {
				CSPEventObserver cspO = (CSPEventObserver) o;
				if (((CSPEventObserver) o).isRestored()
						&& cspO.getAttribute().equals(attribute))
					return true;
			}
		}
		return false;
	}

	private boolean isRestored() {
		return restored;
	}

	private int getSetPosition() {
		return setPosition;
	}

	private int getMaxSetPosition(BControl control) {

		int maxSetPosition = 0;
		for (Observer o : control.getObservers()) {
			if (o instanceof CSPEventObserver) {
				CSPEventObserver cspO = (CSPEventObserver) o;
				if (cspO.getSetPosition() > maxSetPosition
						&& cspO.getAttribute().equals(attribute))
					maxSetPosition = cspO.getSetPosition();
			}
		}
		return maxSetPosition;

	}

	private void runme(OpInfo op, int pos, BControl control, Trace history) {

		if (op == null)
			return;

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

				String cspExpression = expression;

				CSP cspEval = new CSP(cspExpression,
						(CSPModel) history.getModel());

				try {

					EvaluationResult eval = history.evalCurrent(cspEval);
					if (eval != null && !eval.hasError()) {
						String result = eval.value;
						result = result.replace("}", "").replace("{", "");
						String[] split = result.split(",");
						listOfOps = new ArrayList<String>();
						java.util.Collections.addAll(listOfOps, split);
					}

				} catch (ProBError e) {
					logger.error("ProBError " + e.getMessage(), e);
				}

			}

			if (listOfOps != null && listOfOps.contains(opNameWithParameter)) {

				int maxSetPosition = getMaxSetPosition(control);

				if (pos < maxSetPosition && pos != -1)
					return;

				if (isCustom) {
					String parseExpression = parseExpression(value.toString(),
							control, op);
					CSP cspE = new CSP(parseExpression,
							(CSPModel) history.getModel());
					EvaluationResult subEval = history.evalCurrent(cspE);
					if (subEval != null && !subEval.hasError()) {
						control.setAttributeValue(attribute, subEval.value,
								true, false);
					}
				} else {
					control.setAttributeValue(attribute, value, true, false);
				}

				setPosition = pos;

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
