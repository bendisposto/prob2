package de.prob.animator.command;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class BeforeAfterPredicateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "before_after_predicate";
	private static final String BA_PRED_VARIABLE = "BAPredicate";

	private final String operationName;
	private IEvalElement result = null;

	public BeforeAfterPredicateCommand(final String operationName) {
		this.operationName = operationName;
	}

	public IEvalElement getBeforeAfterPredicate() {
		return result;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm compoundTerm = BindingGenerator.getCompoundTerm(bindings.get(BA_PRED_VARIABLE), 0);
		String code = compoundTerm.getFunctor();
		result = new EventB(code);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.printAtom(operationName);
		pout.printVariable(BA_PRED_VARIABLE);
		pout.closeTerm();
	}
}
