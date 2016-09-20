package de.prob.animator.command;

import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.Transition;
import de.prob.statespace.State;

public class FilterStatesForPredicateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "filter_states_for_predicate";
	private final Collection<State> ids;
	private final IEvalElement predicate;
	private static final String FILTERED_VARIABLE = "Filtered";
	private final List<String> filtered = new ArrayList<String>();
	private final List<String> errors = new ArrayList<String>();

	public FilterStatesForPredicateCommand(final IEvalElement predicate,
			final Collection<State> ids) {
		if (!predicate.getKind().equals(PREDICATE.toString())) {
			throw new IllegalArgumentException(
					"Formula in GetStatesFromPredicate must be a predicate");
		}
		this.predicate = predicate;
		this.ids = ids;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		predicate.printProlog(pto);
		pto.openList();
		for (State id : ids) {
			pto.printAtomOrNumber(id.getId());
		}
		pto.closeList();
		pto.printVariable(FILTERED_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm term = bindings.get(FILTERED_VARIABLE);
		if (term.hasFunctor("errors", 1)) {
			PrologTerm errorL = BindingGenerator.getCompoundTerm(term, 1)
					.getArgument(1);
			ListPrologTerm errList = BindingGenerator.getList(errorL);
			for (PrologTerm prologTerm : errList) {
				errors.add(prologTerm.getFunctor());
			}
		} else {
			ListPrologTerm list = BindingGenerator.getList(term);
			for (PrologTerm prologTerm : list) {
				filtered.add(Transition.getIdFromPrologTerm(prologTerm));
			}
		}
	}

	public List<String> getFiltered() {
		return filtered;
	}

	public List<String> getErrors() {
		return errors;
	}
}
