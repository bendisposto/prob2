package de.prob.model.representation;

import com.google.inject.Inject
import com.google.inject.Provider

import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.statespace.FormalismType
import de.prob.statespace.StateSpace
import de.prob.statespace.Trace

public abstract class AbstractModel extends AbstractElement {

	private StateSpace stateSpace = null;
	private final Provider<StateSpace> stateSpaceProvider;
	protected boolean dirty = false;
	protected File modelFile;
	protected String modelDirPath;
	protected DependencyGraph graph = new DependencyGraph();
	protected Map<String, AbstractElement> components = new HashMap<String, AbstractElement>();
	def static Closure subscribe = null

	@Inject
	def AbstractModel(Provider<StateSpace> stateSpaceProvider) {
		this.stateSpaceProvider = stateSpaceProvider
	}

	/**
	 * @return StateSpace object associated with this AbstractModel instance
	 */
	public StateSpace getStateSpace() {
		if (stateSpace == null) {
			stateSpace = stateSpaceProvider.get();
			stateSpace.setModel(this);
		}
		return stateSpace;
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

	public Object asType(final Class<?> className) {
		if (className.getSimpleName().equals("StateSpace")) {
			return getStateSpace();
		}
		if (className.getSimpleName().equals("Trace")) {
			return new Trace(getStateSpace());
		}
		throw new ClassCastException("No element of type " + className
		+ " found.");
	}

	public abstract AbstractElement getMainComponent();

	public abstract IEvalElement parseFormula(String formula);

	public abstract FormalismType getFormalismType();

	protected void extractModelDir(File modelFile, String dirName) {
		modelDirPath = modelFile.absolutePath.substring(0, modelFile.absolutePath.lastIndexOf(File.separator)+1) + dirName + File.separator
		new File(modelDirPath).mkdir()
	}

	public File getModelFile() {
		return modelFile;
	}

	public String getModelDirPath() {
		return modelDirPath;
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
}
