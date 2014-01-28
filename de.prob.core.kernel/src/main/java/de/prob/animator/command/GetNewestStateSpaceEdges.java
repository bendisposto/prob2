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

public class GetNewestStateSpaceEdges extends AbstractCommand {

	public static final String NEW = "New";
	private static final int OP_ARITY = 3;
	private final long last;
	private List<OpInfo> newOps;

	public GetNewestStateSpaceEdges(final long last) {
		this.last = last;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_statespace_edges_newer_than");
		pto.printNumber(last);
		pto.printVariable(NEW);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		newOps = new ArrayList<OpInfo>();
		long last = this.last;
		for (PrologTerm prologTerm : (ListPrologTerm) bindings.get(NEW)) {
			CompoundPrologTerm op = BindingGenerator.getCompoundTerm(
					prologTerm, OP_ARITY);
			String id = OpInfo.getIdFromPrologTerm(op.getArgument(1));
			String src = OpInfo.getIdFromPrologTerm(op.getArgument(2));
			String dest = OpInfo.getIdFromPrologTerm(op.getArgument(3));
			newOps.add(new OpInfo(id, src, dest));
		}
	}

	public List<OpInfo> getNewOps() {
		return newOps;
	}

}
