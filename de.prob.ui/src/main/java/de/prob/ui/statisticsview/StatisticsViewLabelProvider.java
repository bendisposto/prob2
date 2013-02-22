package de.prob.ui.statisticsview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.prob.statespace.StateSpace;

public class StatisticsViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	StateSpace currentS;

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof String) {
				return (String) element;
			}
		}

		if (columnIndex == 1) {
			if (element.equals("Calculated States")) {
				return "" + currentS.getExplored().size();
			} else if (element.equals("Total States")) {
				return "" + currentS.vertexSet().size();
			} else if (element.equals("Transitions")) {
				return "" + currentS.edgeSet().size();
			}
		}
		return "";
	}

	public void setCurrentS(final StateSpace currentStateSpace) {
		currentS = currentStateSpace;
	}

}
