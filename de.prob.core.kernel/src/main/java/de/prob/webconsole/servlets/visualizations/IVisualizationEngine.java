package de.prob.webconsole.servlets.visualizations;

import de.prob.visualization.Transformer;

/**
 * @author joy
 * 
 *         Classes that implement {@link IVisualizationEngine} are classes that
 *         calculate the data necessary to display a visualization coded using
 *         the D3 JavaScript library. One {@link IVisualizationEngine}
 *         corresponds to exactly one visualization.
 */
public interface IVisualizationEngine {

	/**
	 * The user may define a {@link Transformer} object that defines the styling
	 * that should be applied to a visualization. This method applies the
	 * styling to the visualization.
	 * 
	 * @param styling
	 */
	public void apply(Transformer styling);
}
