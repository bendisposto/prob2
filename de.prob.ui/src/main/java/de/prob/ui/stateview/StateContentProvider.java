package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.StateSchema;
import de.prob.statespace.History;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
public class StateContentProvider implements ITreeContentProvider {

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
		if (currentHistory != null) {
			AbstractModel model = currentHistory.getModel();
			if (model != null) {
				StateSchema schema = model.getStateSchema();
				if (schema != null) {
					return schema.getElements(inputElement);
				}
			}
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(final Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (currentHistory != null) {
			AbstractModel model = currentHistory.getModel();
			if (model != null) {
				StateSchema schema = model.getStateSchema();
				if (schema != null) {
					return schema.hasChildren(element);
				}
			}
		}
		return false;
	}

	public void setCurrentHistory(final History currentHistory) {
		this.currentHistory = currentHistory;
	}
}