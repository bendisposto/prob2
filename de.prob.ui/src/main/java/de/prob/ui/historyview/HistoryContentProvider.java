package de.prob.ui.historyview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class HistoryContentProvider implements IStructuredContentProvider {

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(final Object inputElement) {
		List<Object> ops = new ArrayList<Object>();
		if( inputElement instanceof History) {
			History history = (History) inputElement;
			HistoryElement current = history.getCurrent();
			while( current.getPrevious() != null ) {
				ops.add(current.getOp());
				current = current.getPrevious();
			}
		}
		return ops.toArray();
	}
}