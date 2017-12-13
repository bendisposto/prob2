package de.prob.model.brules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.model.brules.ComputationResults.RESULT;
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
		final Set<AbstractOperation> operationsToBeEvaluated = abstractOperation.getTransitiveDependencies();
		operationsToBeEvaluated.add(abstractOperation);

		Set<RuleOperation> rules = AbstractOperation.filterOperations(operationsToBeEvaluated, RuleOperation.class);
		RuleResults ruleResults = new RuleResults(rules, t.getCurrentState(), 0);
		Set<ComputationOperation> comps = AbstractOperation.filterOperations(operationsToBeEvaluated,
				ComputationOperation.class);
		ComputationResults compResult = new ComputationResults(comps, t.getCurrentState());

		List<AbstractOperation> executionOrder = abstractOperation.getSortedListOfTransitiveDependencies();
		executionOrder.add(abstractOperation);
		List<AbstractOperation> operationsToBeExecuted = new ArrayList<>();
		for (AbstractOperation dep : executionOrder) {
			if (dep instanceof RuleOperation) {
				RuleOperation ruleOp = (RuleOperation) dep;
				RuleResult ruleResult = ruleResults.getRuleResult(ruleOp.getName());
				if (ruleResult.getResultEnum() == RuleResult.RESULT_ENUM.NOT_CHECKED) {
					operationsToBeExecuted.add(dep);
				}
			} else {
				ComputationOperation compOp = (ComputationOperation) dep;
				RESULT result = compResult.getResult(compOp);
				if (result == RESULT.NOT_EXECUTED) {
					operationsToBeExecuted.add(dep);
				}
			}
		}
		for (AbstractOperation op : operationsToBeExecuted) {
			try {
				List<Transition> transitions = t.getStateSpace()
						.getTransitionsBasedOnParameterValues(t.getCurrentState(), op.getName(), new ArrayList<>(), 1);
				t = t.add(transitions.get(0));
			} catch (ProBError e) {
				break;
			}
		}
		return t;
	}

}
