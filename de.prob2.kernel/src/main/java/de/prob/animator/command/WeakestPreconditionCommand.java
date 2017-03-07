package de.prob.animator.command;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class WeakestPreconditionCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_weakest_precondition";
	private static final String WEAKEST_PRECONDITION_VARIABLE = "WeakestPrecondition";

	private final String operationName;
	private final IEvalElement predicate;
	private IEvalElement result = null;

	public WeakestPreconditionCommand(final String operationName, final IEvalElement predicate) {
		this.operationName = operationName;
		this.predicate = predicate;
	}

	public IEvalElement getWeakestPrecondition() {
		return result;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm compoundTerm = BindingGenerator.getCompoundTerm(bindings.get(WEAKEST_PRECONDITION_VARIABLE),
				0);
		String code = compoundTerm.getFunctor();
		if (predicate instanceof EventB) {
			result = new EventB(code);
		} else {
			result = new ClassicalB(code);
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.printAtom(operationName);
		predicate.printProlog(pout);
		pout.printVariable(WEAKEST_PRECONDITION_VARIABLE);
		pout.closeTerm();
	}
}
