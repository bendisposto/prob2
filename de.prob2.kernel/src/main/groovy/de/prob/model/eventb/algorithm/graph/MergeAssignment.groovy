package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.Statement

class MergeAssignment implements IGraphTransformer {

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
		if (node instanceof IAssignment) {
			Set<Edge> inEdges = g.inEdges(node)
			def toMerge = !inEdges.isEmpty()
			inEdges.each { Edge e ->
				toMerge = toMerge && (!e.conditions.isEmpty() && e.assignment == null)
			}
			if (toMerge) {
				Set<Edge> outE = g.outEdges(node)
				assert outE.size() == 1
				Edge oE = outE.first()
				g = g.removeNode(node)
				inEdges.each { Edge e ->
					g = g.addEdge(e.mergeAssignment(oE))
				}
				return merge(g, oE.to)
			}
		}

		g.outEdges(node).each { Edge e ->
			g = merge(g, e.to)
		}
		return g
	}
}
