package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ExpandFormulaCommand implements ICommand {

	private IEvalElement formula;
	private String stateId;
	private final String TREE = "TREE";
	private PrologTerm prologTerm;

	public ExpandFormulaCommand(IEvalElement formula, String stateId) {
		this.formula = formula;
		this.stateId = stateId;	
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("prob2_formula_expand");
		pto.printAtomOrNumber(stateId);
		pto.openTerm("eval");
		formula.printProlog(pto);
		pto.printAtom(formula.getKind().toString());
		pto.closeTerm();
		pto.printVariable(TREE);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		prologTerm = bindings.get(TREE);
	}
	
	public PrologTerm getResult() {
		return prologTerm;
	}

}
