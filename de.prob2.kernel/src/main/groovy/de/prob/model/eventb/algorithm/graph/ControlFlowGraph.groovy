package de.prob.model.eventb.algorithm.graph

import com.github.krukow.clj_lang.PersistentHashMap
import com.github.krukow.clj_lang.PersistentHashSet
import com.github.krukow.clj_lang.PersistentVector;

import de.be4.ltl.core.parser.node.THistorically;
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.transform.AssertionExtractor
import de.prob.util.Tuple2;

class ControlFlowGraph {
	//Map<Statement, Set<Assertion>> properties

	final PersistentHashSet<Statement> nodes
	final PersistentHashMap<Statement, Set<Edge>> outgoingEdges
	final PersistentHashMap<Statement, Set<Edge>> incomingEdges
	final Block algorithm
	final Statement entryNode
	final Statement lastNode

	def ControlFlowGraph(Block b) {
		def cfg = create(b)
		nodes = cfg.nodes
		outgoingEdges = cfg.outgoingEdges
		incomingEdges = cfg.incomingEdges
		algorithm = cfg.algorithm
		entryNode = cfg.entryNode
		lastNode = cfg.lastNode
	}

	def ControlFlowGraph(algorithm, entryNode, lastNode, nodes, outgoingEdges, incomingEdges) {
		this.algorithm = algorithm
		this.entryNode = entryNode
		this.lastNode = lastNode
		this.nodes = nodes
		this.outgoingEdges = outgoingEdges
		this.incomingEdges = incomingEdges
	}

	def static ControlFlowGraph create(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			def cfg = new ControlFlowGraph(algorithm, algorithm.statements.first(), algorithm.statements.last(), PersistentHashSet.emptySet(), PersistentHashMap.emptyMap(), PersistentHashMap.emptyMap())
			return cfg.addNode(algorithm.statements.first(), algorithm.statements.tail())
		}
		new ControlFlowGraph(algorithm, null, null, PersistentHashSet.emptySet(), PersistentHashMap.emptyMap(), PersistentHashMap.emptyMap())
	}

	def ControlFlowGraph addEdge(Statement from, Statement to, PersistentVector<Tuple2<Statement, EventB>> conditions) {
		def newNodes = nodes.plus(from).plus(to)
		Edge e = new Edge(from, to, conditions)
		def oE = outgoingEdges
		if (oE[from] == null) {
			oE = oE.plus(from, PersistentHashSet.emptySet())
		}
		def iE = incomingEdges
		if (iE[to] == null) {
			iE = iE.plus(to, PersistentHashSet.emptySet())
		}
		oE = oE.plus(from, oE[from].plus(e))
		iE = iE.plus(to, iE[to].plus(e))
		return new ControlFlowGraph(algorithm, from, lastNode, newNodes, oE, iE)
	}

	def ControlFlowGraph removeNode(Statement node) {
		def newNodes = nodes.minus(node)
		def oE = outgoingEdges.minus(node)
		oE.each { Statement s, Set<Edge> edges ->
			edges.each { Edge e ->
				if (e.to == node) {
					oE = oE.plus(s, edges.minus(e))
				}
			}
		}
		def iE = incomingEdges.minus(node)
		iE.each { Statement s, Set<Edge> edges ->
			edges.each { Edge e ->
				if (e.from == node) {
					iE = iE.plus(s, edges.minus(e))
				}
			}
		}
		if (entryNode == node && outgoingEdges[node].size() != 1) {
			throw new IllegalArgumentException("Could not delete entry node, since unambiguous new entry node")
		}
		def eN = entryNode == node ? outgoingEdges[node].first() : entryNode
		return new ControlFlowGraph(algorithm, eN, lastNode, newNodes, oE, iE)
	}

	def ControlFlowGraph addNode(Assignment a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), outgoingEdges, incomingEdges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Call a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), outgoingEdges, incomingEdges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Return a, List<Statement> stmts) {
		addEdge(a, lastNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Skip a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), outgoingEdges, incomingEdges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, lastNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Assertion a, List<Statement> stmts) {
		assert !stmts.isEmpty() // assertions are mapped to the next statement, so an assertion before empty statements is incorrect
		addNode(stmts.first(), stmts.tail())
	}

	def ControlFlowGraph loopToNode(ControlFlowGraph subgraph, Statement dest) {
		ControlFlowGraph g = this
		nodes.each { n ->
			if (!subgraph.outgoingEdges.containsKey(n)) {
				g = g.addEdge(n, dest, PersistentVector.emptyVector())
			}
		}
		g
	}

	def ControlFlowGraph addNode(While w, List<Statement> stmts) {
		List<Statement> block = w.block.statements
		if (block.isEmpty()) {
			throw new IllegalArgumentException("While loops cannot be null!")
		}
		ControlFlowGraph cfg1 = addNode(block.first(), block.tail())
		cfg1.nodes.each { n ->
			if (!cfg1.outgoingEdges.containsKey(n)) {
				if (n instanceof Filler) {
					Set<Edge> inEdges = cfg1.incomingEdges[n]
					cfg1 = cfg1.removeNode(n)
					inEdges.each { Edge e ->
						cfg1 = cfg1.addEdge(e.from, w, e.conditions)
					}
				} else {
					cfg1 = cfg1.addEdge(n, w, PersistentVector.emptyVector())
				}
			}
		}
		cfg1 = cfg1.addEdge(w, block.first(), newCondition(w, w.condition))

		if (!stmts.isEmpty()) {
			def cfg2 = cfg1.addNode(stmts.first(), stmts.tail())
			cfg2 = cfg2.addEdge(w, cfg2.entryNode, newCondition(w, w.notCondition))
			return cfg2
		}
		return cfg1.addEdge(w, new Filler(), newCondition(w, w.notCondition))
	}

	def PersistentVector<Tuple2<Statement,EventB>> newCondition(Statement s, EventB condition) {
		PersistentVector.emptyVector().plus(new Tuple2<Statement,EventB>(s,condition))
	}

	def ControlFlowGraph addNode(If i, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			Filler f = new Filler()
			if (i.Then.statements.isEmpty() && i.Else.statements.isEmpty()) {
				return addEdge(i, f, newCondition(i, i.condition)).addEdge(i, f, newCondition(i, i.elseCondition))
			}
			if (i.Then.statements.isEmpty()) {
				def cfg = addNode(i.Else.statements.first(), i.Else.statements.tail())
				return cfg.addEdge(i, cfg.entryNode, newCondition(i, i.elseCondition)).addEdge(i, f, newCondition(i, i.condition))
			}
			if (i.Else.statements.isEmpty()) {
				def cfg = addNode(i.Then.statements.first(), i.Then.statements.tail())
				return cfg.addEdge(i, cfg.entryNode, newCondition(i, i.condition)).addEdge(i, f, newCondition(i, i.elseCondition))
			}
			def cfgt = addNode(i.Then.statements.first(), i.Then.statements.tail())
			def cfge = cfgt.addNode(i.Else.statements.first(), i.Else.statements.tail())
			return cfge.addEdge(i, cfgt.entryNode, newCondition(i, i.condition)).addEdge(i, cfge.entryNode, newCondition(i, i.elseCondition))
		}

		if (i.Then.statements.isEmpty() && i.Else.statements.isEmpty()) {
			def nextG = addNode(stmts.first(), stmts.tail())
			return nextG.addEdge(i, nextG.entryNode, newCondition(i, i.condition)).addEdge(i, nextG.entryNode, newCondition(i, i.elseCondition))
		}
		if (i.Then.statements.isEmpty()) {
			def cfg = addNode(i.Else.statements.first(), i.Else.statements.tail())
			cfg = cfg.addEdge(i, cfg.entryNode, newCondition(i, i.elseCondition))
			def nextG = cfg.addNode(stmts.first(), stmts.tail())
			def f = nextG.entryNode

			cfg.nodes.each { n ->
				if (!cfg.outgoingEdges.containsKey(n)) {
					if (n instanceof Filler) {
						Set<Edge> inEdges = nextG.incomingEdges[n]
						nextG = nextG.removeNode(n)
						inEdges.each { Edge e ->
							nextG = nextG.addEdge(e.from, f, e.conditions)
						}
					} else {
						nextG = nextG.addEdge(n, f, PersistentVector.emptyVector())
					}
				}
			}
			return nextG.addEdge(i, f, newCondition(i, i.condition))
		}
		if (i.Else.statements.isEmpty()) {
			def cfg = addNode(i.Then.statements.first(), i.Then.statements.tail())
			cfg = cfg.addEdge(i, cfg.entryNode, newCondition(i, i.condition))
			def nextG = cfg.addNode(stmts.first(), stmts.tail())
			def f = nextG.entryNode

			cfg.nodes.each { n ->
				if (!cfg.outgoingEdges.containsKey(n)) {
					if (n instanceof Filler) {
						Set<Edge> inEdges = nextG.incomingEdges[n]
						nextG = nextG.removeNode(n)
						inEdges.each { Edge e ->
							nextG = nextG.addEdge(e.from, f, e.conditions)
						}
					} else {
						nextG = nextG.addEdge(n, f, PersistentVector.emptyVector())
					}
				}
			}
			return nextG.addEdge(i, f, newCondition(i, i.elseCondition))
		}
		def cfg1 = addNode(i.Then.statements.first(), i.Then.statements.tail())
		def cfg2 = cfg1.addNode(i.Else.statements.first(), i.Else.statements.tail())
		def nextG = cfg2.addNode(stmts.first(), stmts.tail())
		def f = nextG.entryNode
		cfg2.nodes.each { n ->
			if (!cfg2.outgoingEdges.containsKey(n)) {
				if (n instanceof Filler) {
					Set<Edge> inEdges = nextG.incomingEdges[n]
					nextG = nextG.removeNode(n)
					inEdges.each { Edge e ->
						nextG = nextG.addEdge(e.from, f, e.conditions)
					}
				} else {
					nextG = nextG.addEdge(n, f, PersistentVector.emptyVector())
				}
			}
		}

		nextG.addEdge(i, cfg1.entryNode, newCondition(i, i.condition)).addEdge(i, cfg2.entryNode, newCondition(i, i.elseCondition))
	}

	def size() {
		return nodes.size()
	}

	def outEdges(Statement stmt) {
		outgoingEdges[stmt] ?: []
	}

	def inEdges(Statement stmt) {
		incomingEdges[stmt] ?: []
	}

	private class Filler extends Statement	{
		def Filler() {
			super([] as Set)
		}
	}
}
