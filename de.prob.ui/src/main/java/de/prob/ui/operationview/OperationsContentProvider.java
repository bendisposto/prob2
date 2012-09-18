package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.History;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class OperationsContentProvider implements IStructuredContentProvider {

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(final Object inputElement) {
		List<Object> ops= new ArrayList<Object>();
		
		if( inputElement instanceof History) {
			History history = (History) inputElement;
			Set<OpInfo> nextTransitions = history.getNextTransitions();
			ops.addAll(nextTransitions);
		}
		
		return ops.toArray();
	}
}