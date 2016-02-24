package de.prob.animator.command;

import de.prob.animator.domainobjects.ExpandedFormula;
import de.prob.animator.domainobjects.FormulaId;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class ExpandFormulaCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "expand_formula";
	private final State stateId;
	private final String TREE = "TREE";
	private final FormulaId id;
	private ExpandedFormula result;

	public ExpandFormulaCommand(final FormulaId id, final State stateId) {
		this.id = id;
		this.stateId = stateId;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(id.getId());
		pto.printAtomOrNumber(stateId.getId());
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
