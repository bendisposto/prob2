package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.model.eventb.EventBComponent;
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

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(final Object inputElement) {

		if (inputElement instanceof Object[]) {
			Object[] elements = (Object[]) inputElement;
			return elements;
		}

		return new ArrayList<Object>().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		List<Object> children = new ArrayList<Object>();

		if (parentElement instanceof EventBComponent) {

			EventBComponent element = (EventBComponent) parentElement;
			if (element.isContext()) {
				List<String> constants = element.getConstants();
				for (String name : constants) {
					children.addAll(extractVariables(name));
				}
			}
			if (element.isMachine()) {
				List<String> variables = element.getVariables();
				for (String name : variables) {
					children.addAll(extractVariables(name));
				}

			}
		}
		return children.toArray();
	}

	private List<Object> extractVariables(String name) {
		StateSpaceInfo info = currentHistory.getStatespace().getInfo();
		List<Object> children = new ArrayList<Object>();
		HistoryElement currentTrans = currentHistory.getCurrent();
		StateId previousState = currentTrans.getSrc();
		StateId currentState = currentTrans.getDest();
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
	public Object getParent(Object element) {
		return element.getClass();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof EventBComponent) {
			return true;
		}
		return false;
	}

	public void setCurrentHistory(History currentHistory) {
		this.currentHistory = currentHistory;
	}
}