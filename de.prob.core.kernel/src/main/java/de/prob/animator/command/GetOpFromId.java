package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class GetOpFromId extends AbstractCommand {

	private final OpInfo op;
	private final String TERM = "JavaOpTerm";

	private String src;
	private String dest;
	private String name;
	private String targetState;
	private final List<String> params = new ArrayList<String>();

	public GetOpFromId(final OpInfo opInfo) {
		this.op = opInfo;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_op_from_id").printAtomOrNumber(op.getId())
				.printVariable(TERM).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TERM);
		CompoundPrologTerm cpt = BindingGenerator
				.getCompoundTerm(prologTerm, 8);

		name = PrologTerm.atomicString(cpt.getArgument(2));
		src = OpInfo.getIdFromPrologTerm(cpt.getArgument(3));
		dest = OpInfo.getIdFromPrologTerm(cpt.getArgument(4));

		ListPrologTerm lpt = BindingGenerator.getList(cpt.getArgument(6));
		for (PrologTerm pt : lpt) {
			params.add(pt.getFunctor());
		}

		targetState = OpInfo.getIdFromPrologTerm(cpt.getArgument(8));
		op.setInfo(name, params, targetState);
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public String getName() {
		return name;
	}

	public List<String> getParams() {
		return params;
	}

	public String getTargetState() {
		return targetState;
	}

}
