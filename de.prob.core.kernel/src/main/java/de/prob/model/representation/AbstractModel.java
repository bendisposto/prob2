package de.prob.model.representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.StateSpace;

public abstract class AbstractModel extends AbstractDomTreeElement {

	protected StateSpace statespace;
	protected HashMap<String, AbstractElement> components;
	protected DirectedMultigraph<String, RefType> graph;

	public StateSpace getStatespace() {
		return statespace;
	}

	public HashMap<String, AbstractElement> getComponents() {
		return components;
	}

	public DirectedMultigraph<String, RefType> getGraph() {
		return graph;
	}

	public ERefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public ERefType getEdge(final String comp1, final String comp2) {
		final RefType edge = graph.getEdge(comp1, comp2);
		if (edge == null)
			return null;

		return edge.getRelationship();
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public String getLabel() {
		return uuid;
	}

	@Override
	public boolean toEvaluate() {
		return false;
	}

	public List<AbstractDomTreeElement> getSubcomponents(
			Collection<AbstractElement> values) {
		final List<AbstractDomTreeElement> subformulas = new ArrayList<AbstractDomTreeElement>();
		for (AbstractElement abstractElement : values) {
			AbstractDomTreeElement adt = (AbstractDomTreeElement) abstractElement;
			subformulas.add(adt);
		}
		return subformulas;
	}

}
