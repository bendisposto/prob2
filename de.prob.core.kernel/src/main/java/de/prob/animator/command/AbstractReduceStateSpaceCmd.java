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

	private static final int STATE_LABELS_INDEX = 4;
	private static final int STATE_COUNT_INDEX = 2;
	private static final int STATE_COLOR_INDEX = 3;
	private static final int STATE_ID_INDEX = 1;
	private static final int OP_ID_INDEX = 1;
	private static final int OP_SRC_INDEX = 2;
	private static final int OP_DEST_INDEX = 3;
	private static final int OP_LABEL_INDEX = 4;
	private static final int OP_STYLE_INDEX = 5;
	private static final int OP_COLOR_INDEX = 6;

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
				String id = OpInfo.getIdFromPrologTerm(cpt
						.getArgument(OP_ID_INDEX));
				String src = OpInfo.getIdFromPrologTerm(cpt
						.getArgument(OP_SRC_INDEX));
				String dest = OpInfo.getIdFromPrologTerm(cpt
						.getArgument(OP_DEST_INDEX));
				String label = cpt.getArgument(OP_LABEL_INDEX).toString();
				DerivedOp op = new DerivedOp(id, src, dest, label);

				String style = cpt.getArgument(OP_STYLE_INDEX).getFunctor();
				String color = cpt.getArgument(OP_COLOR_INDEX).getFunctor();

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

				String id = OpInfo.getIdFromPrologTerm(cpt
						.getArgument(STATE_ID_INDEX));

				List<String> labels = new ArrayList<String>();
				ListPrologTerm ls = BindingGenerator.getList(cpt
						.getArgument(STATE_LABELS_INDEX));
				for (PrologTerm pt : ls) {
					labels.add(pt.getFunctor());
				}
				int count = BindingGenerator
						.getInteger(cpt.getArgument(STATE_COUNT_INDEX))
						.getValue().intValue();

				DerivedStateId stateId = new DerivedStateId(id, labels, count);

				String color = cpt.getArgument(STATE_COLOR_INDEX).getFunctor()
						.toString();

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
