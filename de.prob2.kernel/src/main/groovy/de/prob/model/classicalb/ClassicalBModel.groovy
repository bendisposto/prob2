package de.prob.model.classicalb

import com.github.krukow.clj_lang.PersistentHashMap

import com.google.inject.Inject

import de.be4.classicalb.core.parser.BParser
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader
import de.be4.classicalb.core.parser.exceptions.BCompoundException
import de.be4.classicalb.core.parser.exceptions.BParseException
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

class ClassicalBModel extends AbstractModel {

	private final ClassicalBMachine mainMachine
	private final BParser bparser
	private final RecursiveMachineLoader rml

	@Inject
	ClassicalBModel(final StateSpaceProvider ssProvider) {
		super(ssProvider,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), null)
		this.mainMachine = null
		this.bparser = null
		this.rml = null
	}

	ClassicalBModel(final StateSpaceProvider ssProvider,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
			DependencyGraph graph, File modelFile, BParser bparser, RecursiveMachineLoader rml,
			ClassicalBMachine mainMachine) {
		super(ssProvider, children, graph, modelFile)
		this.bparser = bparser
		this.rml = rml
		this.mainMachine = mainMachine
	}

	ClassicalBModel create(final Start mainAST, final RecursiveMachineLoader rml, final File modelFile,
			final BParser bparser) {
		DependencyGraph graph = new DependencyGraph()

		final DomBuilder d = new DomBuilder(null)
		ClassicalBMachine classicalBMachine = d.build(mainAST)

		ModelElementList<ClassicalBMachine> machines = new ModelElementList<>()
		machines = machines.addElement(classicalBMachine)
		graph = graph.addVertex(classicalBMachine.getName())

		final Set<LinkedList<TIdentifierLiteral>> vertices = new HashSet<>()
		vertices.add(d.getMachineId())
		final Set<LinkedList<TIdentifierLiteral>> done = new HashSet<>()
		boolean fpReached = false

		while (!fpReached) {
			fpReached = true
			Set<LinkedList<TIdentifierLiteral>> newVertices = new HashSet<>()
			for (final LinkedList<TIdentifierLiteral> machineId : vertices) {
				String machineName = machineId.getLast().getText()
				final Start ast = rml.getParsedMachines().get(machineName)
				if (!done.contains(machineId)) {
					DependencyWalker walker = new DependencyWalker(machineId, machines, graph, rml.getParsedMachines())
					ast.apply(walker)
					graph = walker.getGraph()
					machines = walker.getMachines()
					newVertices.addAll(walker.getMachineIds())
					done.add(machineId)
					fpReached = false
				}
			}
			vertices.addAll(newVertices)
		}

		return new ClassicalBModel(stateSpaceProvider, assoc(Machine.class, machines), graph, modelFile, bparser, rml,
				classicalBMachine)
	}

	ClassicalBMachine getMainMachine() {
		return mainMachine
	}

	@Override
	IEvalElement parseFormula(final String formula, final FormulaExpand expand) {
		try {
			return new ClassicalB(bparser.parseFormula(formula), expand)
		} catch (BCompoundException e) {
			if (e.cause?.cause instanceof BParseException) {
				throw new EvaluationException(((BParseException)e.cause.cause).realMsg, e)
			} else {
				throw new EvaluationException(e)
			}
		}
	}

	@Override
	FormalismType getFormalismType() {
		return FormalismType.B
	}

	@Override
	boolean checkSyntax(final String formula) {
		try {
			parseFormula(formula)
			return true
		} catch (EvaluationException e) {
			return false
		}
	}

	@Override
	StateSpace load(final AbstractElement mainComponent, final Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent, preferences,
				new LoadBProjectCommand(rml, modelFile))
	}

	@Override
	AbstractElement getComponent(String name) {
		return getChildrenOfType(Machine.class).getElement(name)
	}

	@Override
	Object getProperty(String name) {
		AbstractElement component = getComponent(name)
		if (component != null) {
			return component
		}
		return super.getProperty(name)
	}

	Object getAt(String name) {
		return getComponent(name)
	}
}
