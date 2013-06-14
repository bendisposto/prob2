package de.prob.ui.bunitview;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Multimap;

public class TestViewContentProvider implements ITreeContentProvider {

	Multimap<String, Object> tests;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		if (tests != null) {
			if (inputElement instanceof Set<?>) {
				return ((Set<?>) inputElement).toArray();
			}
		}
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return new Object[] { inputElement };
	}

	public void update(final Multimap<String, Object> tests) {
		this.tests = tests;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof String) {
			Collection<Object> children = tests.get((String) parentElement);
			return children.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(final Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof String) {
			return true;
		}
		return false;
	}
}
