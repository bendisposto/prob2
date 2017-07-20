package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class RegisterFormulaCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "register_prob2_formula";
	private final IEvalElement formula;

	public RegisterFormulaCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		formula.getFormulaId().printUUID(pto);
		if (this.formula instanceof ProBEvalElement) {
			pto.openTerm("eval_typed");
			formula.printProlog(pto);
			pto.printAtom(formula.expansion().name());
		} else {
			pto.openTerm("eval");
			formula.printProlog(pto);
			pto.printAtom(formula.getKind());
			pto.printAtom(formula.expansion().name());
		}
		pto.closeTerm();
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
