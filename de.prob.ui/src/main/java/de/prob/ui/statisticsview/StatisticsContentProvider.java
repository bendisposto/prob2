package de.prob.ui.statisticsview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.statespace.StateSpace;

public class StatisticsContentProvider implements IStructuredContentProvider {

	StateSpace currentS;
	private Map<String, Integer> opNames;

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
		if (opNames != null) {
			elements.add("Operation Coverage:");
			elements.addAll(opNames.entrySet());
		}
		return elements.toArray();
	}

	public void reset(final StateSpace currentStateSpace) {
		currentS = currentStateSpace;
		opNames = new HashMap<String, Integer>();
	}

	public void addOp(final String opName) {
		if (opNames.containsKey(opName)) {
			int count = opNames.get(opName).intValue();
			int newCount = count + 1;
			opNames.put(opName, new Integer(newCount));
		} else {
			opNames.put(opName, 1);
		}
	}
}
