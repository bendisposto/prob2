package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class MergeConditionals implements IGraphTransformer {

	Set<Statement> visited = new HashSet<Statement>()

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		if (graph.entryNode == null) {
			return graph
		}
		merge(graph, graph.entryNode)
	}

	def ControlFlowGraph merge(ControlFlowGraph graph, Statement node) {
		if (visited.contains(node)) {
			return graph;
		}
		visited << node
		ControlFlowGraph g = graph
		if (node instanceof While || node instanceof If) {
			g = mergeBranches(graph, node)
		}
		g.outEdges(node).each { Edge e ->
			g = merge(g, e.to)
		}
		return g
	}

	def ControlFlowGraph mergeBranches(ControlFlowGraph graph, Statement s) {
		ControlFlowGraph g = graph
		boolean done = false
		while(!done) {
			done = true
			Set<Edge> outE = g.outEdges(s)
			outE.each { Edge e ->
				if (e.to instanceof If && e.assignment == null) {
					Set<Edge> ifEdges = graph.outEdges(e.to)
					g = g.removeNode(e.to)
					ifEdges.each { Edge e2 ->
						g = g.addEdge(e.mergeConditions(e2))
					}
					done = false
				}
			}
		}
		return g
	}
}
