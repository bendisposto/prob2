package de.prob.animator.command;

import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class InsertFormulaForVisualizationCommand extends AbstractCommand {

	private final IEvalElement formula;
	private final String ID = "Id";
	private FormulaId formulaId;

	public InsertFormulaForVisualizationCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_insert_formula");
		formula.printProlog(pto);
		pto.printAtom("user");
		pto.printVariable(ID);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(ID);

		formulaId = new FormulaId(prologTerm.getFunctor());
	}

	public FormulaId getFormulaId() {
		return formulaId;
	}

}
