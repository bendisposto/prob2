package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While
import de.prob.model.representation.ModelElementList

class GraphMerge implements IGraphTransformer {

	def ControlFlowGraph graph

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		this.graph = new ControlFlowGraph(new Block()) // creates empty graph
		if (graph.entryNode) {
			this.graph.nodeMapping = graph.nodeMapping
			this.graph.namingWAssertions = graph.namingWAssertions
			this.graph.algorithm = graph.algorithm
			this.graph.woAssertions = graph.woAssertions
			this.graph.assertions = graph.assertions
			this.graph.entryNode = mergeGraph(graph, graph.entryNode)
		} else {
			this.graph = graph
		}
		return this.graph
	}

	def Statement mergeGraph(ControlFlowGraph g, Assignments a) {
		if (graph.nodes.contains(a)) {
			return a
		}
		graph.nodes.add(a)
		g.outEdges(a).each { Edge e ->
			Statement stmt = mergeGraph(g, e.to)
			graph.addEdge(a, stmt, [], e.loopToWhile)
		}
		return a
	}

	def Statement mergeGraph(ControlFlowGraph g, While w) {
		if (graph.nodes.contains(w)) {
			return w
		}
		graph.nodes.add(w)
		Edge condE = g.outEdges(w).find { Edge e -> e.conditions == [w.condition]}
		Edge notE = g.outEdges(w).find { Edge e -> e.conditions == [w.notCondition]}
		mergeIfs(g, w, new ModelElementList([w]), new ModelElementList([w.condition]), condE.to)
		mergeIfs(g, w, new ModelElementList([w]), new ModelElementList([w.notCondition]), notE.to)
		return w
	}

	def Statement mergeGraph(ControlFlowGraph g, If i) {
		if (graph.nodes.contains(i)) {
			return i
		}
		graph.nodes.add(i)
		Edge ifE = g.outEdges(i).find { Edge e -> e.conditions == [i.condition]}
		Edge elseE = g.outEdges(i).find { Edge e -> e.conditions == [i.elseCondition]}
		mergeIfs(g, i, new ModelElementList([i]), new ModelElementList([i.condition]), ifE.to)
		mergeIfs(g, i, new ModelElementList([i]), new ModelElementList([i.elseCondition]), elseE.to)
		return i
	}

	def mergeIfs(ControlFlowGraph g, Statement startNode, ModelElementList<Statement> statements, ModelElementList<EventB> conditions, Statement nextStmt) {
		if (nextStmt instanceof If) {
			Edge ifE = g.outEdges(nextStmt).find { Edge e -> e.conditions == [nextStmt.condition]}
			Edge elseE = g.outEdges(nextStmt).find { Edge e -> e.conditions == [nextStmt.elseCondition]}
			mergeIfs(g, startNode, statements.addElement(nextStmt), conditions.addElement(nextStmt.condition), ifE.to)
			mergeIfs(g, startNode, statements.addElement(nextStmt), conditions.addElement(nextStmt.elseCondition), elseE.to)
		} else {
			Edge oldEdge = g.outEdges(statements.last()).find { Edge e -> e.to == nextStmt }
			Statement stmt = mergeGraph(g, nextStmt)
			Edge e = graph.addEdge(startNode, stmt, conditions, oldEdge.loopToWhile)
			graph.edgeMapping[e] = statements
		}
	}


}
