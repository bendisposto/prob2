package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class RegisterFormulaCommand extends AbstractCommand {

	private final IEvalElement formula;

	public RegisterFormulaCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("register_prob2_formula");
		formula.getFormulaId().printUUID(pto);
		pto.openTerm("eval");
		formula.printProlog(pto);
		pto.printAtom(formula.getKind().toString());
		pto.closeTerm();

		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
