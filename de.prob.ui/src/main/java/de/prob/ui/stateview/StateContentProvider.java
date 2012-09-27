package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.statespace.History;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class StateContentProvider implements IStructuredContentProvider {

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(final Object inputElement) {
		List<Object> vars= new ArrayList<Object>();
		
		if( inputElement instanceof History) {
			History history = (History) inputElement;
			HashMap<String, String> state = history.getStatespace().getInfo().getState(history.getCurrent().getCurrentState());
			for (String key : state.keySet()) {
				vars.add(new Variable(key, state.get(key)));
			}
		}
		
		return vars.toArray();
	}
}