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
import de.prob.statespace.derived.DerivedOp;
import de.prob.statespace.derived.DerivedStateId;

public class ApplySignatureMergeCommand extends AbstractCommand {

	public final String SPACE = "StateSpace";
	public final List<DerivedStateId> states = new ArrayList<DerivedStateId>();
	public final List<DerivedOp> ops = new ArrayList<DerivedOp>();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_signature_merge_state_space");
		pto.printVariable(SPACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// Result term is a list with two arguments [States,Transitions].
		ListPrologTerm list = BindingGenerator.getList(bindings.get(SPACE));

		extractStates(BindingGenerator.getList(list.getArgument(1)));
		extractTransitions(BindingGenerator.getList(list.getArgument(2)));
	}

	// Transitions take the form trans(src,label,dest,color,style)
	private void extractTransitions(final ListPrologTerm trans) {
		for (PrologTerm pt : trans) {
			if (pt instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) pt;
				String src = OpInfo.getIdFromPrologTerm(cpt.getArgument(1));
				String label = cpt.getArgument(2).toString();
				String count = cpt.getArgument(3).getFunctor();
				String dest = OpInfo.getIdFromPrologTerm(cpt.getArgument(4));
				String id = OpInfo.getIdFromPrologTerm(cpt.getArgument(5));
				ops.add(new DerivedOp(id, src, dest, label, count));
			}
		}

	}

	// States take the form state(Signature,Id,InvOk)
	private void extractStates(final ListPrologTerm s) {
		for (PrologTerm prologTerm : s) {
			if (prologTerm instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) prologTerm;
				String label = cpt.getArgument(1).toString();
				String count = cpt.getArgument(2).getFunctor();
				String witness = cpt.getArgument(3).getFunctor();
				String id = OpInfo.getIdFromPrologTerm(cpt.getArgument(4));
				states.add(new DerivedStateId(id, label, witness, count));
			}
		}

	}

	public List<DerivedStateId> getStates() {
		return states;
	}

	public List<DerivedOp> getOps() {
		return ops;
	}

}
