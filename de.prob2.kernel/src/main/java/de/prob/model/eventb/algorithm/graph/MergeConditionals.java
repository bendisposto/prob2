package de.prob.model.eventb.algorithm.graph;

import java.util.HashSet;
import java.util.Set;

import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;

public class MergeConditionals implements IGraphTransformer {
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
		if (node instanceof While || node instanceof If) {
			g = mergeBranches(graph, node);
		}
		
		for (Edge e : g.outEdges(node)) {
			g = merge(g, e.getTo());
		}
		return g;
	}

	public ControlFlowGraph mergeBranches(final ControlFlowGraph graph, Statement s) {
		ControlFlowGraph g = graph;
		boolean done = false;
		while (!done) {
			done = true;
			final Set<Edge> outE = g.outEdges(s);
			for (Edge e : outE) {
				if (e.getTo() instanceof If && e.getAssignment() == null) {
					final Set<Edge> ifEdges = graph.outEdges(e.getTo());
					g = g.removeNode(e.getTo());
					for (Edge e2 : ifEdges) {
						g = g.addEdge(e.mergeConditions(e2));
					}
					done = false;
				}
			}
		}

		return g;
	}
}
