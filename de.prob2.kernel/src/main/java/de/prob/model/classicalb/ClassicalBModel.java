package de.prob.model.classicalb;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.inject.Inject;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.NoContentProvider;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class ClassicalBModel extends AbstractModel {

	private final ClassicalBMachine mainMachine;
	private final HashSet<String> done = new HashSet<String>();
	private final BParser bparser;
	private final RecursiveMachineLoader rml;

	@Inject
	public ClassicalBModel(final StateSpaceProvider ssProvider) {
		super(ssProvider);
		this.mainMachine = null;
		this.bparser = null;
		this.rml = null;
	}

	ClassicalBModel(final StateSpaceProvider ssProvider, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
			DependencyGraph graph,
			File modelFile,
			BParser bparser,
			RecursiveMachineLoader rml,
			ClassicalBMachine mainMachine) {
		super(ssProvider, children, graph, modelFile);
		this.bparser = bparser;
		this.rml = rml;
		this.mainMachine = mainMachine;
	}

	public ClassicalBModel create(final Start mainast,
			final RecursiveMachineLoader rml, final File modelFile,
			final BParser bparser) {
		DependencyGraph graph = new DependencyGraph();

		final DomBuilder d = new DomBuilder(false);
		ClassicalBMachine mainMachine = d.build(mainast);

		ModelElementList<ClassicalBMachine> machines = new ModelElementList<ClassicalBMachine>();
		machines = machines.addElement(mainMachine);
		graph = graph.addVertex(mainMachine.getName());

		boolean fpReached;

		do {
			fpReached = true;
			final Set<String> vertices = graph.getVertices();
			for (final String machineName : vertices) {
				final Start ast = rml.getParsedMachines().get(machineName);
				if (!done.contains(machineName)) {
					DependencyWalker walker = new DependencyWalker(machineName, machines,
							graph, rml.getParsedMachines());
					ast.apply(walker);
					graph = walker.getGraph();
					machines = walker.getMachines();
					done.add(machineName);
					fpReached = false;
				}
			}
		} while (!fpReached);

		return new ClassicalBModel(stateSpaceProvider, (PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>) children.assoc(Machine.class, machines) ,graph, modelFile, bparser, rml, mainMachine);
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

	@Override
	public IEvalElement parseFormula(final String formula) {
		try {
			return new ClassicalB(bparser.parse(BParser.FORMULA_PREFIX + " "
					+ formula, false, new NoContentProvider()));
		} catch (BException e) {
			throw new EvaluationException(e.getMessage());
		}
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
	}

	@Override
	public boolean checkSyntax(final String formula) {
		try {
			parseFormula(formula);
			return true;
		} catch (EvaluationException e) {
			return false;
		}
	}

	@Override
	public StateSpace load(final AbstractElement mainComponent,
			final Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent,
				preferences, new LoadBProjectCommand(rml, modelFile));
	}

	@Override
	public AbstractElement getComponent(String name) {
		return getChildrenOfType(Machine.class).getElement(name);
	}
}
