package de.prob.animator.command;

import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.Transition;

public class GetStatesFromPredicate extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_states_for_predicate";
	private final IEvalElement formula;
	private final String STATES = "States";
	private final String ERRORS = "Errors";
	private final List<String> ids = new ArrayList<String>();

	public GetStatesFromPredicate(final IEvalElement e) {
		if (!e.getKind().equals(PREDICATE.toString())) {
			throw new IllegalArgumentException(
					"Formula in GetStatesFromPredicate must be a predicate");
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
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm list = BindingGenerator.getList(bindings.get(STATES));

		for (PrologTerm prologTerm : list) {
			ids.add(Transition.getIdFromPrologTerm(prologTerm));
		}

	}

	public List<String> getIds() {
		return ids;
	}

}
