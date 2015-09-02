package de.prob.animator.command;

import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class InsertFormulaForVisualizationCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "insert_formula_for_expansion";
	private final IEvalElement formula;
	private final String ID = "Id";
	private FormulaId formulaId;

	public InsertFormulaForVisualizationCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		formula.printProlog(pto);
		pto.printVariable(ID);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(ID);

		formulaId = new FormulaId(prologTerm.getFunctor(), formula);
	}

	public FormulaId getFormulaId() {
		return formulaId;
	}

}
