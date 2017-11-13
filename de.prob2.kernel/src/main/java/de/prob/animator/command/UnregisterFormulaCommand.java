package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class UnregisterFormulaCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "unregister_prob2_formula";
	private final IEvalElement formula;

	public UnregisterFormulaCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		formula.getFormulaId().printUUID(pto);
		if (this.formula instanceof ProBEvalElement) {
			pto.openTerm("eval_typed");
			formula.printProlog(pto);
			pto.printAtom(formula.expansion().getPrologName());
		} else {
			pto.openTerm("eval");
			formula.printProlog(pto);
			pto.printAtom(formula.getKind().getPrologName());
			pto.printAtom(formula.expansion().getPrologName());
		}
		pto.closeTerm();
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
