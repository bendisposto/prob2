package de.bmotionstudio.core.model.observer;

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
import de.prob.scripting.CSPModel;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;

public class CSPEventObserver extends Observer {

	private String expression;

	private String attribute;
	
	private Object value;
	
	private boolean isCustom;
	
	private static final Pattern PATTERN = Pattern.compile("\\$(.+?)\\$");
	
	@Override
	public void check(History history, BControl control) {

		String AsImplodedString = "";

		HistoryElement current = history.getCurrent();
		OpInfo op = current.getOp();
		
		if (op != null) {
			String opName = op.getName();
			List<String> opParameter = op.getParams();
			if (opParameter.size() > 0) {
				String[] inputArray = opParameter
						.toArray(new String[opParameter.size()]);
				StringBuffer sb = new StringBuffer();
				sb.append(inputArray[0]);
				for (int i = 1; i < inputArray.length; i++) {
					sb.append(".");
					sb.append(inputArray[i]);
				}
				AsImplodedString = "." + sb.toString();
			}
			String cspExpression = "bmsresult = member(" + opName
					+ AsImplodedString + "," + expression + ")";
			
			System.out.println("===> " + cspExpression);
			
			CSP cspEval = new CSP(cspExpression, (CSPModel) history.getModel());
			EvaluationResult eval = history.eval(cspEval);
			
			if (eval != null && !eval.hasError()) {

				String result = eval.value;
				Boolean bResult = Boolean.valueOf(result);
				if (bResult) {
//					String parseExpression = parseExpression(value.toString(),
//							control, history);
//					
//					CSP cspE = new CSP(parseExpression, (CSPModel) history.getModel());
//					EvaluationResult eval2 = history.eval(cspE);
					
//					if (eval2 != null && !eval2.hasError()) {
						control.setAttributeValue(attribute, value);
//					}
					
				}

			}
			
			
		}

	}
	
	private String parseExpression(String expressionString,
			BControl control, History history) {

		String finalExpression = expressionString;
		
		OpInfo op = history.getCurrent().getOp();
		List<String> params = op.getParams();
		
		// Find expressions and collect ExpressionEvalElements
		final Matcher matcher = PATTERN.matcher(expressionString);
		while (matcher.find()) {
			
			
			
			int subExpr = Integer.valueOf(matcher.group(1));
			

			
			String para = params.get(subExpr);
			
			System.out.println("==> "+ para);
			
			if(para != null)
				finalExpression = finalExpression.replace("$"+subExpr+"$", para);
		}
		
		System.out.println(finalExpression);
		return finalExpression;

//		// We have only one expression (without "$$")
//		if (!hasSubExpressions) {
//			collectEvalElements(expressionString, expressionString,
//					isPredicate, animation, control, evaluationKeys,
//					parseControls);
//		}
//
//		// Try to get expression results and parse expression string
//		Collection<EvaluationStateElement> resultList;
//		try {
//			resultList = getExpressionValues(control, animation,
//					new ArrayList<EvaluationElement>(evaluationKeys.keySet()));
//		} catch (ProBException e) {
//			resultList = Collections.emptyList();
//			hasError = true;
//		}

		// If getting ExpressionEvalElement throws no error, try to get
		// expression results
//		String result = expressionString;
//		if (!hasError) {
//			for (EvaluationStateElement stateElement : resultList) {
//				final EvaluationElement evalElement = stateElement.getElement();
//				final String text;
//				if (isPredicate) {
//					text = stateElement.getResult().isPredicateTrue() ? "true"
//							: "false";
//				} else {
//					text = stateElement.getText();
//				}
//				final String subExpression = evaluationKeys.get(evalElement);
//				result = result.replace(subExpression, text);
//			}
//		} else {
//			// if (obj != null)
//			// obj.setHasError(true);
//			// addError(control, animation,
//			// "An error occurred while evaluating expression\\predicate value: \""
//			// + expressionString
//			// + "\". Please check your expression\\predicate.");
//		}

//		return result;

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
	
	public boolean isCustom() {
		return isCustom;
	}

	public void setCustom(boolean isCustom) {
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
