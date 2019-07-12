package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.EvalElementType;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.Transition;

public class GetStatesFromPredicate extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_states_for_predicate";
	private static final String STATES = "States";
	private static final String ERRORS = "Errors";

	private final IEvalElement formula;
	private final List<String> ids = new ArrayList<>();

	public GetStatesFromPredicate(final IEvalElement e) {
		if (!EvalElementType.PREDICATE.equals(e.getKind())) {
			throw new IllegalArgumentException("Formula in GetStatesFromPredicate must be a predicate");
		}
		formula = e;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		formula.printProlog(pto);
		pto.printVariable(STATES);
		pto.printVariable(ERRORS);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		for (PrologTerm prologTerm : BindingGenerator.getList(bindings.get(STATES))) {
			ids.add(Transition.getIdFromPrologTerm(prologTerm));
		}

	}

	public List<String> getIds() {
		return ids;
	}
}
