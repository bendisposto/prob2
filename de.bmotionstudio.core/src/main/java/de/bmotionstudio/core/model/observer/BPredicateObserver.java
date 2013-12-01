package de.bmotionstudio.core.model.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.BPredicateObserverWizard;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.Trace;

public class BPredicateObserver extends Observer {

	private String predicate;

	private String attribute;

	private Object value;

	private transient List<IEvalElement> formulas;

	@Override
	public List<IEvalElement> prepareObserver(final Trace history,
			final BControl control) {
		if (formulas == null) {
			formulas = new ArrayList<IEvalElement>();
		}
		formulas.clear();
		if (predicate != null) {
			formulas.add(new ClassicalB(BMotionUtil.parseFormula(predicate,
					control)));
		}
		return formulas;
	}

	@Override
	public void check(final Trace history, final BControl control,
			final Map<String, IEvalResult> results) {

		if (attribute == null || value == null || predicate == null) {
			return;
		}

		String fpredicate = BMotionUtil.parseFormula(predicate, control);
		IEvalResult evalResult = results.get(fpredicate);
		if (evalResult != null && evalResult instanceof EvalResult) {
			String result = ((EvalResult) evalResult).getValue();
			Boolean bResult = Boolean.valueOf(result);
			if (bResult) {
				control.setAttributeValue(attribute, value, true, false);
			}
		}

	}

	@Override
	public ObserverWizard getWizard(final Shell shell, final BControl control) {
		return new BPredicateObserverWizard(shell, control, this);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(final String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(final String attribute) {
		String oldVal = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldVal, attribute);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		Object oldVal = this.value;
		this.value = value;
		firePropertyChange("value", oldVal, value);
	}

	@Override
	public String getType() {
		return "B Predicate Observer";
	}

	@Override
	public String getDescription() {
		return "This observer sets the value of an attribute whenever the entered predicate was evaluated to true.";
	}

}
