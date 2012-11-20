package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.IEval;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;
import de.prob.statespace.StateSpace;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class StateContentProvider implements ITreeContentProvider {

	public History currentHistory;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	@Override
	public Object[] getElements(final Object inputElement) {

		if (inputElement instanceof Object[]) {
			final Object[] elements = (Object[]) inputElement;
			return elements;
		}
		if (inputElement instanceof AbstractModel) {
			final AbstractModel model = (AbstractModel) inputElement;
			return getChildren(model);
		}

		return new Object[] {};
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		final List<Object> children = new ArrayList<Object>();

		if (parentElement instanceof AbstractElement) {
			AbstractElement element = (AbstractElement) parentElement;
			for (Set<? extends AbstractElement> object : element.getChildren()
					.values()) {
				children.addAll(object);
			}

			for (Object object : children) {
				if (object instanceof IEval) {
					children.set(children.indexOf(object),
							extractValue(((IEval) object).getEvaluate()));
				}
			}
		}
		return children.toArray();
	}

	private Object extractValue(final IEvalElement element) {
		final StateSpace statespace = currentHistory.getS();
		final HistoryElement currentTrans = currentHistory.getCurrent();
		final Map<IEvalElement, String> previousValues = statespace
				.valuesAt(currentTrans.getSrc());
		final Map<IEvalElement, String> currentValues = statespace
				.valuesAt(currentTrans.getDest());

		final Variable var = new Variable(element.getCode(), "", "");
		if (previousValues.containsKey(element)) {
			var.setPreviousValue(previousValues.get(element));
		}
		if (currentValues.containsKey(element)) {
			var.setCurrentValue(currentValues.get(element));
		}
		return var;
	}

	// private List<Object> extractVariables(final String name) {
	// final StateSpaceInfo info = currentHistory.getStatespace().getInfo();
	// final List<Object> children = new ArrayList<Object>();
	// final HistoryElement currentTrans = currentHistory.getCurrent();
	// final StateId previousState = currentTrans.getSrc();
	// final StateId currentState = currentTrans.getDest();
	// if (currentTrans.getOp() != null) {
	// String currentValue = "";
	// String previousValue = "";
	// if (info.stateHasVariable(currentState, name)) {
	// currentValue = info.getVariable(currentState, name);
	// }
	// if (info.stateHasVariable(previousState, name)) {
	// previousValue = info.getVariable(previousState, name);
	// }
	// children.add(new Variable(name, currentValue, previousValue));
	// } else {
	// children.add(new Variable(name, "", ""));
	// }
	// return children;
	// }

	@Override
	public Object getParent(final Object element) {
		return element.getClass();
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof AbstractElement) {
			AbstractElement elem = (AbstractElement) element;
			for (Set<? extends AbstractElement> kids : elem.getChildren()
					.values()) {
				if (!kids.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public void setCurrentHistory(final History currentHistory) {
		this.currentHistory = currentHistory;
	}
}