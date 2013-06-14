package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;
import de.prob.statespace.derived.DerivedOp;
import de.prob.statespace.derived.DerivedStateId;

public abstract class AbstractReduceStateSpaceCmd extends AbstractCommand {

	public final String SPACE = "StateSpace";
	public final List<DerivedStateId> states = new ArrayList<DerivedStateId>();
	public final List<DerivedOp> ops = new ArrayList<DerivedOp>();
	public final Map<String, Set<DerivedStateId>> nodeColors = new HashMap<String, Set<DerivedStateId>>();
	public final Map<String, Set<DerivedOp>> transStyle = new HashMap<String, Set<DerivedOp>>();
	public final Map<String, Set<DerivedOp>> transColor = new HashMap<String, Set<DerivedOp>>();

	// Transitions take the form trans(TransId,Src,Dest,Label,Style,Color)
	protected void extractTransitions(final ListPrologTerm trans) {
		for (PrologTerm pt : trans) {
			if (pt instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) pt;
				String id = OpInfo.getIdFromPrologTerm(cpt.getArgument(1));
				String src = OpInfo.getIdFromPrologTerm(cpt.getArgument(2));
				String dest = OpInfo.getIdFromPrologTerm(cpt.getArgument(3));
				String label = cpt.getArgument(4).toString();
				DerivedOp op = new DerivedOp(id, src, dest, label);

				String style = cpt.getArgument(5).getFunctor();
				String color = cpt.getArgument(6).getFunctor();

				ops.add(op);
				if (transStyle.containsKey(style)) {
					transStyle.get(style).add(op);
				} else {
					Set<DerivedOp> ids = new HashSet<DerivedOp>();
					ids.add(op);
					transStyle.put(style, ids);
				}

				if (transColor.containsKey(color)) {
					transColor.get(color).add(op);
				} else {
					Set<DerivedOp> ids = new HashSet<DerivedOp>();
					ids.add(op);
					transColor.put(color, ids);
				}
			}
		}

	}

	// States take the form node(NodeId,Count,Color,Labels)
	protected void extractStates(final ListPrologTerm s) {
		for (PrologTerm prologTerm : s) {
			if (prologTerm instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) prologTerm;

				String id = OpInfo.getIdFromPrologTerm(cpt.getArgument(1));

				List<String> labels = new ArrayList<String>();
				ListPrologTerm ls = BindingGenerator
						.getList(cpt.getArgument(4));
				for (PrologTerm pt : ls) {
					labels.add(pt.getFunctor());
				}
				int count = BindingGenerator.getInteger(cpt.getArgument(2))
						.getValue().intValue();

				DerivedStateId stateId = new DerivedStateId(id, labels, count);

				String color = cpt.getArgument(3).getFunctor().toString();

				states.add(stateId);
				if (nodeColors.containsKey(color)) {
					nodeColors.get(color).add(stateId);
				} else {
					Set<DerivedStateId> ids = new HashSet<DerivedStateId>();
					ids.add(stateId);
					nodeColors.put(color, ids);
				}
			}
		}
	}

	public List<DerivedStateId> getStates() {
		return states;
	}

	public List<DerivedOp> getOps() {
		return ops;
	}

	public Map<String, Set<DerivedStateId>> getNodeColors() {
		return nodeColors;
	}

	public Map<String, Set<DerivedOp>> getTransStyle() {
		return transStyle;
	}

	public Map<String, Set<DerivedOp>> getTransColor() {
		return transColor;
	}

}
