package de.prob.ui.junitview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class JUnitContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return new Object[0];
	}
}