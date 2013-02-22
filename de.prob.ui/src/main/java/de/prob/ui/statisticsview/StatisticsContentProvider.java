package de.prob.ui.statisticsview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.statespace.StateSpace;

public class StatisticsContentProvider implements IStructuredContentProvider {

	StateSpace currentS;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		List<Object> elements = new ArrayList<Object>();
		if (currentS != null) {
			elements.add("Calculated States");
			elements.add("Total States");
			elements.add("Transitions");
		}
		return elements.toArray();
	}

	public void reset(final StateSpace currentStateSpace) {
		currentS = currentStateSpace;
	}
}
