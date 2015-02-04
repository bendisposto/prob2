package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.DotEdge;
import de.prob.animator.domainobjects.DotNode;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.Transition;

public abstract class AbstractDotDiagramCmd extends AbstractCommand {

	public final String SPACE = "StateSpace";
	private final Map<String, DotNode> nodes = new HashMap<String, DotNode>();
	private final Map<String, DotEdge> edges = new HashMap<String, DotEdge>();

	protected void extractStates(final ListPrologTerm s) {

		for (PrologTerm prologTerm : s) {
			if (prologTerm instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) prologTerm;
				String id = Transition.getIdFromPrologTerm(cpt.getArgument(1));
				List<String> labels = new ArrayList<String>();
				ListPrologTerm ls = BindingGenerator
						.getList(cpt.getArgument(4));
				for (PrologTerm pt : ls) {
					labels.add(pt.getFunctor());
				}
				int count = BindingGenerator.getInteger(cpt.getArgument(2))
						.getValue().intValue();
				String color = cpt.getArgument(3).getFunctor().toString();
				DotNode n = new DotNode(id, labels, count, color);
				nodes.put(id, n);
			}
		}

	}

	protected void extractTransitions(final ListPrologTerm trans) {
		for (PrologTerm pt : trans) {
			if (pt instanceof CompoundPrologTerm) {
				CompoundPrologTerm cpt = (CompoundPrologTerm) pt;
				String id = Transition.getIdFromPrologTerm(cpt.getArgument(1));
				String src = Transition.getIdFromPrologTerm(cpt.getArgument(2));
				String dest = Transition
						.getIdFromPrologTerm(cpt.getArgument(3));
				String label = cpt.getArgument(4).toString();
				String style = cpt.getArgument(5).getFunctor();
				String color = cpt.getArgument(6).getFunctor();

				DotEdge e = new DotEdge(id, src, dest, label, style, color);
				edges.put(id, e);
			}
		}

	}

	public Map<String, DotNode> getNodes() {
		return nodes;
	}

	public Map<String, DotEdge> getEdges() {
		return edges;
	}

}