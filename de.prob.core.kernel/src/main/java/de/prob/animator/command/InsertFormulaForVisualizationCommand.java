package de.prob.animator.command;

import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class InsertFormulaForVisualizationCommand implements ICommand {

	private IEvalElement formula;
	private final String ID = "Id";
	private FormulaId formulaId;

	public InsertFormulaForVisualizationCommand(IEvalElement formula) {
		this.formula = formula;	
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("evaluation_insert_formula");
		formula.printProlog(pto);
		pto.printAtom("user");
		pto.printVariable(ID);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(ID);
		
		formulaId = new FormulaId(prologTerm.getFunctor());
	}
	
	public FormulaId getFormulaId() {
		return formulaId;
	}

}
