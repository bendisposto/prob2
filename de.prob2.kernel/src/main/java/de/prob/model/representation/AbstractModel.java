package de.prob.model.representation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

import groovy.util.Eval;

public abstract class AbstractModel extends AbstractElement {

	protected final StateSpaceProvider stateSpaceProvider;
	protected File modelFile;
	protected final DependencyGraph graph;

	public AbstractModel(
			StateSpaceProvider stateSpaceProvider,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
			DependencyGraph graph, File modelFile) {
		super(children);
		this.stateSpaceProvider = stateSpaceProvider;
		this.graph = graph;
		this.modelFile = modelFile;
	}

	public abstract AbstractElement getComponent(final String name);

	public DependencyGraph getGraph() {
		return graph;
	}

	public ERefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public ERefType getEdge(final String comp1, final String comp2) {
		final List<ERefType> edges = graph.getRelationships(comp1, comp2);
		if (edges.isEmpty()) {
			return null;
		}
		return edges.get(0);
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	/**
	 * Will parse a formula including information specific to the model at hand.
	 *
	 * @param formula to be parsed
	 * @param expand the expansion behavior to use
	 * @return a valid formula
	 * @throws RuntimeException if parsing is not successful
	 */
	public abstract IEvalElement parseFormula(String formula, FormulaExpand expand);
	
	/**
	 * Will parse a formula including information specific to the model at hand.
	 *
	 * @param formula to be parsed
	 * @return a valid formula
	 * @throws RuntimeException if parsing is not successful
	 */
	public IEvalElement parseFormula(String formula) {
		return this.parseFormula(formula, FormulaExpand.truncate);
	}

	/**
	 * Will check the syntax of a formula to see if it is valid in the scope of
	 * this model.
	 *
	 * @param formula
	 *            to be checked
	 * @return whether or not the formula in question has valid syntax in the
	 *         scope of this model
	 */
	public abstract boolean checkSyntax(String formula);

	public abstract FormalismType getFormalismType();

	public File getModelFile() {
		return modelFile;
	}

	public StateSpaceProvider getStateSpaceProvider() {
		return stateSpaceProvider;
	}

	public Object getProperty(String name) {
		if ("stateSpaceProvider".equals(name)) {
			return getStateSpaceProvider();
		}
		if ("modelFile".equals(name)) {
			return getModelFile();
		}
		if ("graph".equals(name)) {
			return getGraph();
		}
		if ("children".equals(name)) {
			return getChildren();
		}
		return null;
	}

	public AbstractElement get(List<String> path) {
		if (path.isEmpty()) {
			return null;
		}
		String p = "x" + "." + Joiner.on(".").join(path);
		return (AbstractElement) Eval.x(this, p);
	}

	public StateSpace load(AbstractElement mainComponent) {
		return load(mainComponent, new HashMap<String, String>());
	}

	public abstract StateSpace load(AbstractElement mainComponent,
			Map<String, String> preferences);
}
