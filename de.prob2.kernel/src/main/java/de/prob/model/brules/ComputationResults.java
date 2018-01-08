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

public class ComputationResults {

	
	private HashMap<String, ComputationState> results = new HashMap<>();

	public ComputationResults(RulesProject project, State state) {
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

	public ComputationResults(Set<ComputationOperation> computations, State state) {
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
			this.results.put(comp.getName(), convertStringToEnum(evalResult));
		}
	}

	private ComputationState convertStringToEnum(EvalResult evalResult) {
		String res = evalResult.toString();
		res = res.substring(1, res.length() - 1);
		return ComputationState.valueOf(res);
	}

	public Map<String, ComputationState> getResults() {
		return new HashMap<>(this.results);
	}

	public ComputationState getResult(String compName) {
		return results.get(compName);
	}

	public ComputationState getResult(ComputationOperation comp) {
		return results.get(comp.getName());
	}

	@Override
	public String toString() {
		return results.toString();
	}

}
