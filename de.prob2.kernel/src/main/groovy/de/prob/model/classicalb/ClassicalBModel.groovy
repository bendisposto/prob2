package de.prob.model.classicalb

import com.github.krukow.clj_lang.PersistentHashMap

import com.google.inject.Inject

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.NoContentProvider
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.be4.classicalb.core.parser.exceptions.BCompoundException
import de.be4.classicalb.core.parser.node.*

import de.prob.animator.command.LoadBProjectCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvaluationException
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.AbstractModel
import de.prob.model.representation.DependencyGraph
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.scripting.StateSpaceProvider
import de.prob.statespace.FormalismType
import de.prob.statespace.StateSpace

public class ClassicalBModel extends AbstractModel {

	private final ClassicalBMachine mainMachine;
	private final BParser bparser;
	private final RecursiveMachineLoader rml;

	@Inject
	public ClassicalBModel(final StateSpaceProvider ssProvider) {
		super(ssProvider, PersistentHashMap.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(), new DependencyGraph(), null);
		this.mainMachine = null;
		this.bparser = null;
		this.rml = null;
	}

	public ClassicalBModel(final StateSpaceProvider ssProvider, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
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

		final DomBuilder d = new DomBuilder(null);
		ClassicalBMachine mainMachine = d.build(mainast);

		ModelElementList<ClassicalBMachine> machines = new ModelElementList<ClassicalBMachine>();
		machines = machines.addElement(mainMachine);
		graph = graph.addVertex(mainMachine.getName());


		final Set<LinkedList<TIdentifierLiteral>> vertices = new HashSet<LinkedList<TIdentifierLiteral>>();
		vertices.add(d.getMachineId())
		final Set<LinkedList<TIdentifierLiteral>> done = new HashSet<LinkedList<TIdentifierLiteral>>();
		boolean fpReached = false;

		while(!fpReached) {
			fpReached = true;
			Set<LinkedList<TIdentifierLiteral>> newVertices = new HashSet<LinkedList<TIdentifierLiteral>>()
			for (final LinkedList<TIdentifierLiteral> machineId : vertices) {
				String machineName = machineId.getLast().getText();
				final Start ast = rml.getParsedMachines().get(machineName);
				if (!done.contains(machineId)) {
					DependencyWalker walker = new DependencyWalker(machineId, machines,
							graph, rml.getParsedMachines());
					ast.apply(walker);
					graph = walker.getGraph();
					machines = walker.getMachines();
					newVertices.addAll(walker.getMachineIds());
					done.add(machineId);
					fpReached = false;
				}
			}
			vertices.addAll(newVertices)
		}

		return new ClassicalBModel(stateSpaceProvider, assoc(Machine.class, machines) ,graph, modelFile, bparser, rml, mainMachine);
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

	@Override
	public IEvalElement parseFormula(final String formula, final FormulaExpand expand) {
		final String prefixedFormula = BParser.FORMULA_PREFIX + "\n" + formula;
		try {
			//TODO replace by parseFormula(formula) when new parser is released
			return new ClassicalB(bparser.parse(prefixedFormula, false, new NoContentProvider()), expand);
		} catch (BCompoundException e) {
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

	@Override
	public Object getProperty(String name) {
		def component = getComponent(name);
		if (component) {
			return component
		}
		return super.getProperty(name);
	}

	public Object getAt(String name) {
		getComponent(name);
	}
}
