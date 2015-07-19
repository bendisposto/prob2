package de.prob.model.representation;

import com.google.inject.Inject

import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.scripting.StateSpaceProvider
import de.prob.statespace.FormalismType
import de.prob.statespace.StateSpace

public abstract class AbstractModel extends AbstractElement {

	protected final StateSpaceProvider stateSpaceProvider;
	protected boolean dirty = false;
	protected File modelFile;
	protected String modelDirPath;
	protected DependencyGraph graph = new DependencyGraph();
	protected Map<String, AbstractElement> components = new HashMap<String, AbstractElement>();
	def static Closure subscribe = null

	@Inject
	def AbstractModel(StateSpaceProvider stateSpaceProvider) {
		this.stateSpaceProvider = stateSpaceProvider
	}
	
	public String getModelDirPath() {
		return modelDirPath;
	}

	public AbstractElement getComponent(final String name) {
		return components.get(name);
	}

	public Map<String, AbstractElement> getComponents() {
		return components;
	}

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

		return edges.first();
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	public abstract IEvalElement parseFormula(String formula);

	public abstract boolean checkSyntax(String formula);

	public abstract FormalismType getFormalismType();

	public File getModelFile() {
		return modelFile;
	}

	def getProperty(final String name) {
		return components.get(name)
	}

	def getAt(final String name) {
		return components.get(name)
	}

	def setDirty() {
		dirty = true
	}

	def isDirty() {
		return dirty
	}

	def AbstractElement get(List<String> path) {
		if(path.isEmpty()) {
			return null;
		}
		return Eval.x(this,"x.${path.join(".")}");
	}

	def Closure getClosure() {
		return AbstractModel.subscribe
	}

	public StateSpace load(AbstractElement mainComponent) {
		load(mainComponent, [:])
	}

	public abstract StateSpace load(AbstractElement mainComponent, Map<String,String> preferences);
}
