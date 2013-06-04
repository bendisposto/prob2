package de.prob.ui.stateview;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;
import de.prob.model.representation.IEval;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Variable;
import de.prob.statespace.StateId;
import de.prob.statespace.Trace;

class StateViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public Trace currentTrace;

	@Override
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			if (obj instanceof Entry<?, ?>) {
				Object value = ((Entry<?, ?>) obj).getKey();
				if (value instanceof Class<?>) {
					return getHeaderName((Class<?>) value);
				}
			}
			return obj.toString();
		}

		if (index == 1 && obj instanceof IEval) {
			return getValue(((IEval) obj), currentTrace.getCurrentState());
		}

		if (index == 2 && obj instanceof IEval) {
			return getValue(((IEval) obj), currentTrace.getCurrent().getSrc());
		}
		return "";
	}

	private String getHeaderName(final Class<?> value) {
		if (value.equals(Invariant.class)) {
			return "Invariants";
		}
		if (value.equals(Variable.class)) {
			return "Variables";
		}
		if (value.equals(BSet.class)) {
			return "Sets";
		}
		if (value.equals(Constant.class)) {
			return "Constants";
		}
		if (value.equals(Axiom.class)) {
			return "Axioms";
		}
		return "";
	}

	private String getValue(final IEval o, final StateId state) {
		EvaluationResult result;
		if (o instanceof Constant) {
			result = ((Constant) o).getValue(currentTrace);
		} else {
			Map<IEvalElement, EvaluationResult> values = currentTrace
					.getStateSpace().valuesAt(state);
			result = values.get(o.getEvaluate());
		}
		return result != null ? result.value : "";
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		return null;
	}

	@Override
	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public void setInput(final Trace currentTrace) {
		this.currentTrace = currentTrace;

	}

}