package de.prob.animator.command;

import de.prob.animator.domainobjects.ExpandedFormula;
import de.prob.animator.domainobjects.FormulaId;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ExpandFormulaCommand extends AbstractCommand {

	private final String stateId;
	private final String TREE = "TREE";
	private final FormulaId id;
	private ExpandedFormula result;

	public ExpandFormulaCommand(final FormulaId id, final String stateId) {
		this.id = id;
		this.stateId = stateId;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_formula_expand");
		pto.printAtomOrNumber(id.getId());
		pto.printAtomOrNumber(stateId);
		pto.printVariable(TREE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(
				bindings.get(TREE), 4);
		result = new ExpandedFormula(cpt);
	}

	public ExpandedFormula getResult() {
		return result;
	}

}
