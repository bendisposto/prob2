package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetOpFromId extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_op_from_id";
	private final Transition op;
	private static final String PARAMETERS_VARIABLE = "Params";
	private static final String RETURNVALUES_VARIABLE = "RetVals";
	private List<String> params;
	private List<String> returnValues;
	private final FormulaExpand expansion;

	public GetOpFromId(final Transition opInfo, final FormulaExpand expansion) {
		op = opInfo;
		this.expansion = expansion;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtomOrNumber(op.getId())
				.printAtom(expansion.getPrologName()).printVariable(PARAMETERS_VARIABLE)
				.printVariable(RETURNVALUES_VARIABLE).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm plist = BindingGenerator.getList(bindings.get(PARAMETERS_VARIABLE));
		params = Collections.emptyList();
		if (!plist.isEmpty()) {
			params = new ArrayList<String>();
		}
		for (PrologTerm p : plist) {
			params.add(p.getFunctor().intern());
		}

		ListPrologTerm rlist = BindingGenerator.getList(bindings.get(RETURNVALUES_VARIABLE));
		returnValues = Collections.emptyList();
		if (!rlist.isEmpty()) {
			returnValues = new ArrayList<String>();
		}
		for (PrologTerm r : rlist) {
			returnValues.add(r.getFunctor().intern());
		}

		op.setInfo(expansion, params, returnValues);
	}

	
	/**
	 * @deprecated Use getParameters() instead
	 */
	@Deprecated
	public List<String> getParams() {
		return params;
	}
	
	
	
	public List<String> getParameters() {
		return params;
	}
	
	
	

	public List<String> getReturnValues() {
		return returnValues;
	}

}
