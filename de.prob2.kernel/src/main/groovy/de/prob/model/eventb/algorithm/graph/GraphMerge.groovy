package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.representation.ModelElementList

class GraphMerge implements IGraphTransformer {

	def ControlFlowGraph graph
	def AlgorithmGenerationOptions options

	def GraphMerge(AlgorithmGenerationOptions options) {
	}

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		this.graph = new ControlFlowGraph(new Block()) // creates empty graph
		if (graph.entryNode) {
			this.graph.nodeMapping = graph.nodeMapping
			this.graph.algorithm = graph.algorithm
			this.graph.properties = graph.properties
			this.graph.loopsForTermination = graph.loopsForTermination.collectEntries { w,l ->
				[w, []]
			}
			this.graph.loopToWhile = graph.loopToWhile.collectEntries { w,l ->
				[w, []]
			}
			//		this.graph.loopsForTermination = graph.loopsForTermination
			this.graph.entryNode = mergeGraph(graph, graph.entryNode)
		} else {
			this.graph = graph
		}
		return this.graph
	}

	def Statement mergeGraph(ControlFlowGraph g, IAssignment a) {
		if (graph.nodes.contains(a)) {
			return a
		}
		graph.nodes.add(a)
		g.outEdges(a).each { Edge e ->
			Statement stmt = mergeGraph(g, e.to)
			def nE = graph.addEdge(a, stmt, [])
			if (g.loopsForTermination[e.to] != null && g.loopsForTermination[e.to].contains(e)) {
				graph.loopsForTermination[e.to] << nE
			}
			if (g.loopToWhile[e.to] != null && g.loopToWhile[e.to].contains(e)) {
				graph.loopToWhile[e.to] << nE
			}
		}
		return a
	}

	def Statement mergeGraph(ControlFlowGraph g, Skip s) {
		if (graph.nodes.contains(s)) {
			return s
		}
		graph.nodes.add(s)
		g.outEdges(s).each { Edge e ->
			Statement stmt = mergeGraph(g, e.to)
			def nE = graph.addEdge(s, stmt, [])
			if (g.loopsForTermination[e.to] != null && g.loopsForTermination[e.to].contains(e)) {
				graph.loopsForTermination[e.to] << nE
			}
			if (g.loopToWhile[e.to] != null && g.loopToWhile[e.to].contains(e)) {
				graph.loopToWhile[e.to] << nE
			}
		}
		return s
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
			List<If> merged1 = mergeIfs(g, startNode, statements.addElement(nextStmt), conditions.addElement(nextStmt.condition), ifE.to)
			List<If> merged2 = mergeIfs(g, startNode, statements.addElement(nextStmt), conditions.addElement(nextStmt.elseCondition), elseE.to)
		} else {
			Edge oldEdge = g.outEdges(statements.last()).find { Edge e -> e.to == nextStmt }
			Statement stmt = mergeGraph(g, nextStmt)
			Edge e = graph.addEdge(startNode, stmt, conditions)
			graph.edgeMapping[e] = statements
			if (g.loopsForTermination[oldEdge.to] != null && g.loopsForTermination[oldEdge.to].contains(oldEdge)) {
				graph.loopsForTermination[e.to] << e
			}
			if (g.loopToWhile[oldEdge.to] != null && g.loopToWhile[oldEdge.to].contains(oldEdge)) {
				graph.loopToWhile[e.to] << e
			}
		}
	}


}
