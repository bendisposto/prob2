package de.prob.model.representation;

import java.util.Map.Entry

import com.google.inject.Inject
import com.google.inject.Provider

import de.prob.animator.command.AbstractCommand
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.SetPreferenceCommand
import de.prob.animator.command.StartAnimationCommand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.statespace.FormalismType
import de.prob.statespace.StateSpace

public abstract class AbstractModel extends AbstractElement {

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
	protected StateSpace createStateSpace(AbstractElement mainComponent) {
		if (mainComponent == null || !components.containsValue(mainComponent)) {
			throw new IllegalArgumentException("Specified main component: "+mainComponent.toString()+" is not a valid machine or context in this model.");
		}
		StateSpace s = stateSpaceProvider.get()
		s.setModel(this,mainComponent)
		return s;
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

	protected StateSpace loadFromCommand(AbstractElement mainComponent, Map<String,String> preferences, AbstractCommand loadCmd) {
		StateSpace s = createStateSpace(mainComponent);
		List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

		for (Entry<String, String> pref : preferences.entrySet()) {
			cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
		}

		cmds.add(loadCmd);
		cmds.add(new StartAnimationCommand());

		s.execute(new ComposedCommand(cmds));
		return s;
	}
}
