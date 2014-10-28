package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.animator.command.AbstractCommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.util.StringUtil;

public class GetOpFromId extends AbstractCommand {

	private final OpInfo op;
	private final String PARAMS = "Params";
	private final String RETVALS = "RetVals";
	private List<String> params;
	private List<String> returnValues;

	public GetOpFromId(final OpInfo opInfo) {
		op = opInfo;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_op_from_id").printAtomOrNumber(op.getId())
				.printVariable(PARAMS).printVariable(RETVALS).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm plist = BindingGenerator.getList(bindings.get(PARAMS));
		params = Collections.emptyList();
		if (!plist.isEmpty()) {
			params = new ArrayList<String>();
		}
		for (PrologTerm p : plist) {
			params.add(StringUtil.generateString(p.getFunctor()));
		}

		ListPrologTerm rlist = BindingGenerator.getList(bindings.get(RETVALS));
		returnValues = Collections.emptyList();
		if (!rlist.isEmpty()) {
			returnValues = new ArrayList<String>();
		}
		for (PrologTerm r : rlist) {
			returnValues.add(StringUtil.generateString(r.getFunctor()));
		}

		op.setInfo(params, returnValues);
	}

	public List<String> getParams() {
		return params;
	}

	public List<String> getReturnValues() {
		return returnValues;
	}

}
