package de.prob.model.brules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.ComputationOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.State;

public class ComputationStatuses {

	private HashMap<String, ComputationStatus> statuses = new HashMap<>();

	public ComputationStatuses(RulesProject project, State state) {
		this(extractComputationOperations(project), state);
	}

	private static Set<ComputationOperation> extractComputationOperations(RulesProject project) {
		final Set<ComputationOperation> comps = new HashSet<>();
		for (AbstractOperation op : project.getOperationsMap().values()) {
			if (op instanceof ComputationOperation) {
				comps.add((ComputationOperation) op);
			}
		}
		return comps;
	}

	public ComputationStatuses(Set<ComputationOperation> computations, State state) {
		final ArrayList<ComputationOperation> compList = new ArrayList<>();
		final List<IEvalElement> evalElements = new ArrayList<>();
		for (ComputationOperation comp : computations) {
			compList.add(comp);
			ClassicalB ruleObject = new ClassicalB(comp.getName());
			evalElements.add(ruleObject);
		}
		List<AbstractEvalResult> evalResults = state.eval(evalElements);
		for (int i = 0; i < compList.size(); i++) {
			ComputationOperation comp = compList.get(i);
			AbstractEvalResult abstractEvalResult = evalResults.get(i);
			EvalResult evalResult = (EvalResult) abstractEvalResult;
			this.statuses.put(comp.getName(), ComputationStatus.valueOf(evalResult));
		}
	}

	public Map<String, ComputationStatus> getResults() {
		return new HashMap<>(this.statuses);
	}

	public ComputationStatus getResult(String compName) {
		return statuses.get(compName);
	}

	public ComputationStatus getResult(ComputationOperation comp) {
		return statuses.get(comp.getName());
	}

	@Override
	public String toString() {
		return statuses.toString();
	}

}
