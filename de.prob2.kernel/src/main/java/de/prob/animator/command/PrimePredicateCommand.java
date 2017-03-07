package de.prob.animator.command;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class PrimePredicateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_primed_predicate";
	private static final String PRIMED_PREDICATE_VARIABLE = "PrimedPredicate";

	private final IEvalElement evalElement;
	private IEvalElement result = null;

	public PrimePredicateCommand(final IEvalElement evalElement) {
		this.evalElement = evalElement;
	}

	public IEvalElement getPrimedPredicate() {
		return result;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm compoundTerm = BindingGenerator.getCompoundTerm(bindings.get(PRIMED_PREDICATE_VARIABLE), 0);
		String code = compoundTerm.getFunctor();
		if (evalElement instanceof EventB) {
			result = new EventB(code);
		} else {
			result = new ClassicalB(code);
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		evalElement.printProlog(pout);
		pout.printVariable(PRIMED_PREDICATE_VARIABLE);
		pout.closeTerm();
	}
}
