package de.prob.model.eventb.algorithm.graph;

import java.util.HashSet;
import java.util.Set;

import de.prob.model.eventb.algorithm.ast.IAssignment;
import de.prob.model.eventb.algorithm.ast.Statement;

public class MergeAssignment implements IGraphTransformer {

	private final Set<Statement> visited = new HashSet<>();

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		if (graph.getEntryNode().equals(ControlFlowGraph.FILLER)) {
			return graph;
		}

		return merge(graph, graph.getEntryNode());
	}

	public ControlFlowGraph merge(ControlFlowGraph graph, Statement node) {
		if (visited.contains(node)) {
			return graph;
		}

		visited.add(node);
		ControlFlowGraph g = graph;
		if (node instanceof IAssignment) {
			final Set<Edge> inEdges = g.inEdges(node);
			boolean toMerge = !inEdges.isEmpty();
			for (Edge inEdge : inEdges) {
				toMerge = toMerge && !inEdge.getConditions().isEmpty() && inEdge.getAssignment() == null;
			}
			
			if (toMerge) {
				final Set<Edge> outE = g.outEdges(node);
				assert outE.size() == 1;
				final Edge oE = outE.stream().findFirst().orElseThrow(AssertionError::new);
				g = g.removeNode(node);
				for (Edge e : inEdges) {
					g = g.addEdge(e.mergeAssignment(oE));
				}
				return merge(g, oE.getTo());
			}
		}

		for (Edge e : g.outEdges(node)) {
			g = merge(g, e.getTo());
		}
		return g;
	}
}
