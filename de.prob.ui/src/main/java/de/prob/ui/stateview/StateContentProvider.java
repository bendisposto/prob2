package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.model.classicalb.ClassicalBEntity;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.eventb.EventBElement;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpaceInfo;

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

		return new ArrayList<Object>().toArray();
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		final List<Object> children = new ArrayList<Object>();

		if (parentElement instanceof EventBElement) {

			final EventBElement element = (EventBElement) parentElement;
			if (element.isContext()) {
				final List<String> constants = element.getConstantNames();
				for (final String name : constants) {
					children.addAll(extractVariables(name));
				}
			}
			if (element.isMachine()) {
				final List<String> variables = element.getVariableNames();
				for (final String name : variables) {
					children.addAll(extractVariables(name));
				}

			}
		}
		if (parentElement instanceof ClassicalBMachine) {
			final ClassicalBMachine cbMachine = (ClassicalBMachine) parentElement;
			final List<ClassicalBEntity> variables = cbMachine.variables();
			for (final ClassicalBEntity entity : variables) {
				children.addAll(extractVariables(entity.getIdentifier()));
			}
		}
		return children.toArray();
	}

	private List<Object> extractVariables(final String name) {
		final StateSpaceInfo info = currentHistory.getStatespace().getInfo();
		final List<Object> children = new ArrayList<Object>();
		final HistoryElement currentTrans = currentHistory.getCurrent();
		final StateId previousState = currentTrans.getSrc();
		final StateId currentState = currentTrans.getDest();
		if (currentTrans.getOp() != null) {
			String currentValue = "";
			String previousValue = "";
			if (info.stateHasVariable(currentState, name)) {
				currentValue = info.getVariable(currentState, name);
			}
			if (info.stateHasVariable(previousState, name)) {
				previousValue = info.getVariable(previousState, name);
			}
			children.add(new Variable(name, currentValue, previousValue));
		} else {
			children.add(new Variable(name, "", ""));
		}
		return children;
	}

	@Override
	public Object getParent(final Object element) {
		return element.getClass();
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof EventBElement
				|| element instanceof ClassicalBMachine) {
			return true;
		}
		return false;
	}

	public void setCurrentHistory(final History currentHistory) {
		this.currentHistory = currentHistory;
	}
}