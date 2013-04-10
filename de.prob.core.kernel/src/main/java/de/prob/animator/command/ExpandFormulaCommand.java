package de.prob.animator.command;

import de.prob.animator.domainobjects.ExpandedFormula;
import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ExpandFormulaCommand implements ICommand {

	private String stateId;
	private final String TREE = "TREE";
	private PrologTerm prologTerm;
	private FormulaId id;
	private ExpandedFormula result;

	public ExpandFormulaCommand(FormulaId id, String stateId) {
		this.id = id;
		this.stateId = stateId;
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("prob2_formula_expand");
		pto.printAtomOrNumber(id.getId());
		pto.printAtomOrNumber(stateId);
		pto.printVariable(TREE);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(bindings.get(TREE),3);
		result = new ExpandedFormula(cpt);
	}
	
	public ExpandedFormula getResult() {
		return result;
	}

}
