package de.prob.model.brules;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.inject.Inject;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.ComputationOperation;
import de.be4.classicalb.core.parser.rules.FunctionOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.command.LoadRulesProjectCommand;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class RulesModel extends AbstractModel {

	private RulesProject project;
	private Map<AbstractOperation, IEvalElement> operationStateFormulaCache = new HashMap<>();

	@Inject
	public RulesModel(StateSpaceProvider stateSpaceProvider) {
		super(stateSpaceProvider,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), null);
	}

	public RulesModel(StateSpaceProvider stateSpaceProvider, File file, RulesProject project) {
		super(stateSpaceProvider,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), file);
		this.project = project;
	}

	@Override
	public AbstractElement getComponent(String name) {
		throw new AssertionError();
	}

	public RulesModel create(File file, RulesProject project) {
		return new RulesModel(stateSpaceProvider, file, project);
	}

	@Override
	public IEvalElement parseFormula(String formula, FormulaExpand expand) {
		try {
			return new ClassicalB(new BParser().parseFormula(formula), expand);
		} catch (BCompoundException e) {
			throw new EvaluationException(e.getMessage(), e.getFirstException());
		}
	}

	@Override
	public boolean checkSyntax(String formula) {
		try {
			parseFormula(formula);
			return true;
		} catch (EvaluationException e) {
			return false;
		}
	}

	@Override
	public <T extends AbstractElement> ModelElementList<T> getChildrenOfType(final Class<T> c) {
		return new ModelElementList<>();
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
	}

	@Override
	public StateSpace load(AbstractElement mainComponent, Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent, preferences,
				new LoadRulesProjectCommand(project, modelFile));
	}

	public LoadRulesProjectCommand getLoadCommand() {
		return new LoadRulesProjectCommand(project, modelFile);
	}

	public RulesProject getRulesProject() {
		return this.project;
	}

	public IEvalElement getEvalElement(AbstractOperation abstractOperation) {
		if (operationStateFormulaCache.containsKey(abstractOperation)) {
			return operationStateFormulaCache.get(abstractOperation);
		} else {
			String name = abstractOperation.getName();
			IEvalElement evalElement = this.parseFormula(name);
			operationStateFormulaCache.put(abstractOperation, evalElement);
			return evalElement;
		}
	}

	public Map<AbstractOperation, OperationState> evalOperations(State state, List<AbstractOperation> operations) {
		ArrayList<IEvalElement> formulas = new ArrayList<>();
		for (AbstractOperation abstractOperation : operations) {
			if (abstractOperation instanceof ComputationOperation || abstractOperation instanceof RuleOperation) {
				formulas.add(getEvalElement(abstractOperation));
			}
		}
		state.getStateSpace().subscribe(this, formulas);
		Map<IEvalElement, AbstractEvalResult> values = state.getValues();
		final Map<AbstractOperation, OperationState> states = new HashMap<>();
		for (AbstractOperation op : operations) {
			if (op instanceof RuleOperation) {
				states.put(op, RuleState.valueOf(values.get(getEvalElement(op))));
			} else if (op instanceof ComputationOperation) {
				states.put(op, ComputationState.valueOf(values.get(getEvalElement(op))));
			}
		}
		return states;
	}

	public Trace executeOperationAndDependencies(final Trace trace, String opName) {
		if (!project.getOperationsMap().containsKey(opName)) {
			throw new IllegalArgumentException("Unknown operation name: " + opName);
		}
		AbstractOperation abstractOperation = project.getOperationsMap().get(opName);
		if (abstractOperation instanceof FunctionOperation) {
			throw new IllegalArgumentException("Function operations are not supported: " + opName);
		}

		Trace t = trace;
		t.setExploreStateByDefault(false);

		while (!t.getCurrentState().isInitialised()) {
			t = t.anyOperation(null);
		}
		List<AbstractOperation> executionOrder = abstractOperation.getSortedListOfTransitiveDependencies();
		executionOrder.add(abstractOperation);
		executionOrder = executionOrder.stream().filter(op -> !(op instanceof FunctionOperation))
				.collect(Collectors.toList());
		Map<AbstractOperation, OperationState> opStates = evalOperations(t.getCurrentState(), executionOrder);

		List<AbstractOperation> operationsToBeExecuted = new ArrayList<>();
		for (AbstractOperation dep : executionOrder) {
			OperationState operationState = opStates.get(dep);
			if (operationState.isNotExecuted() && !operationState.isDisabled()) {
				operationsToBeExecuted.add(dep);
			}
		}
		for (AbstractOperation op : operationsToBeExecuted) {
			try {
				t.setExploreStateByDefault(false);
				List<Transition> transitions = t.getStateSpace()
						.getTransitionsBasedOnParameterValues(t.getCurrentState(), op.getName(), new ArrayList<>(), 1);
				t = t.add(transitions.get(0));
			} catch (ProBError | IllegalArgumentException e) {
				break;
			}
		}
		return t;
	}

}
