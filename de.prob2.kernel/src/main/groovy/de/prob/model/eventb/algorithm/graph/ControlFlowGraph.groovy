package de.prob.model.eventb.algorithm.graph

import groovy.transform.ToString;

import com.github.krukow.clj_lang.PersistentHashMap
import com.github.krukow.clj_lang.PersistentHashSet
import com.github.krukow.clj_lang.PersistentVector

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.util.Tuple2

class ControlFlowGraph {
	public final static Skip FILLER = new Skip()
	public final static Skip END = new Skip()
	public final static ControlFlowGraph EMPTY = new ControlFlowGraph(new Block().finish(), FILLER, FILLER, PersistentHashSet.emptySet(),  PersistentHashSet.emptySet())

	final PersistentHashSet<Statement> nodes
	final PersistentHashSet<Edge> edges
	final Block algorithm
	final Statement entryNode
	final Statement lastNode

	def ControlFlowGraph(Block b) {
		def cfg = create(b)
		nodes = cfg.nodes
		algorithm = cfg.algorithm
		entryNode = cfg.entryNode
		lastNode = cfg.lastNode
		edges = cfg.edges
	}

	def ControlFlowGraph(algorithm, entryNode, lastNode, nodes, edges) {
		this.algorithm = algorithm
		this.entryNode = entryNode
		this.lastNode = lastNode
		this.nodes = nodes
		this.edges = edges
	}

	def static ControlFlowGraph create(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			def lastStmt = algorithm.statements.last()
			def cfg = new ControlFlowGraph(algorithm, algorithm.statements.first(), algorithm.statements.last(), PersistentHashSet.emptySet(), PersistentHashSet.emptySet())
			cfg = cfg.addNode(algorithm.statements.first(), algorithm.statements.tail())
			return cfg.replaceNode(END, lastStmt)
		}
		EMPTY
	}

	def ControlFlowGraph addEdge(Statement from, Statement to, PersistentVector<Tuple2<Statement, EventB>> conditions) {
		def newNodes = nodes.plus(from).plus(to)
		Edge e = new Edge(from, to, conditions)
		return new ControlFlowGraph(algorithm, from, lastNode, newNodes, edges.plus(e))
	}

	def ControlFlowGraph addEdge(Edge e) {
		def newNodes = nodes.plus(e.from).plus(e.to)
		return new ControlFlowGraph(algorithm, entryNode, lastNode, newNodes, edges.plus(e))
	}

	def ControlFlowGraph removeNode(Statement node) {
		def newNodes = nodes.minus(node)
		Set<Edge> newEdges = PersistentHashSet.emptySet()
		edges.each { Edge e ->
			if (e.from != node && e.to != node) {
				newEdges = newEdges.plus(e)
			}
		}

		def outE = newEdges.findAll { it.from == node }
		if (entryNode == node && outE.size() != 1) {
			throw new IllegalArgumentException("Could not delete entry node, since unambiguous new entry node")
		}
		def eN = entryNode == node ? outE.first() : entryNode
		return new ControlFlowGraph(algorithm, eN, lastNode, newNodes, newEdges)
	}

	def ControlFlowGraph removeEdge(Edge e) {
		return new ControlFlowGraph(algorithm, entryNode, lastNode, nodes, edges.minus(e))
	}

	def ControlFlowGraph addNode(Assignment a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), edges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Call a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), edges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Return a, List<Statement> stmts) {
		addEdge(a, END, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Skip a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), edges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, lastNode, PersistentVector.emptyVector())
	}

	def ControlFlowGraph addNode(Assertion a, List<Statement> stmts) {
		assert !stmts.isEmpty() // assertions are mapped to the next statement, so an assertion before empty statements is incorrect
		addNode(stmts.first(), stmts.tail())
	}

	def PersistentVector<Tuple2<Statement,EventB>> newCondition(Statement s, EventB condition) {
		PersistentVector.emptyVector().plus(new Tuple2<Statement,EventB>(s,condition))
	}

	private ControlFlowGraph createSubgraph(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			def lastStmt = algorithm.statements.last()
			lastStmt = lastStmt instanceof Return ? END : lastStmt
			def cfg = new ControlFlowGraph(algorithm, algorithm.statements.first(), lastStmt, PersistentHashSet.emptySet(), PersistentHashSet.emptySet())
			cfg = cfg.addNode(algorithm.statements.first(), algorithm.statements.tail())
			return cfg
		}
		EMPTY
	}

	def addNode(While w, List<Statement> stmts) {
		if (w.block.statements.isEmpty()) {
			throw new IllegalArgumentException("While loops cannot be empty!")
		}
		ControlFlowGraph bodyG = createSubgraph(w.block)
		ControlFlowGraph nextG = stmts.isEmpty() ? EMPTY : addNode(stmts.first(), stmts.tail())

		ControlFlowGraph g = bodyG.merge(nextG, algorithm, w, nextG.lastNode)
				.addEdge(w, bodyG.entryNode, newCondition(w, w.condition))
		g = g.replaceNode(FILLER, w)
		if (bodyG.lastNode != FILLER && bodyG.lastNode != END) {
			g = g.addEdge(new Edge(bodyG.lastNode, w, PersistentVector.emptyVector()))
		}
		g = g.addEdge(w, nextG.entryNode, newCondition(w, w.notCondition))
		g
	}

	def ControlFlowGraph addNode(If i, List<Statement> stmts) {
		ControlFlowGraph thenG = createSubgraph(i.Then)
		ControlFlowGraph elseG = createSubgraph(i.Else)
		ControlFlowGraph g = thenG.merge(elseG, algorithm, i, thenG.lastNode)
				.addEdge(i, thenG.entryNode, newCondition(i, i.condition))
				.addEdge(i, elseG.entryNode, newCondition(i, i.elseCondition))

		Set<Statement> toConnect = [
			thenG.lastNode,
			elseG.lastNode
		].findAll { it != FILLER && it != END }

		ControlFlowGraph nextG = stmts.isEmpty() ? EMPTY : addNode(stmts.first(), stmts.tail())
		g = g.connect(nextG, toConnect)
		g
	}

	def ControlFlowGraph connect(ControlFlowGraph nextG, Set<Statement> toConnect) {
		ControlFlowGraph g = merge(nextG, algorithm, entryNode, nextG.lastNode)
		g = g.replaceNode(FILLER, nextG.entryNode)
		toConnect.each { Statement stmt ->
			g = g.addEdge(new Edge(stmt, nextG.entryNode, PersistentVector.emptyVector()))
		}
		g
	}

	def ControlFlowGraph replaceNode(Statement stmt, Statement newStmt) {
		def newNodes = nodes.minus(stmt).plus(newStmt)
		def newEdges = PersistentHashSet.emptySet()

		edges.each { Edge e ->
			if (e.from == stmt && e.to == stmt) {
				newEdges = newEdges.plus(new Edge(newStmt, newStmt, e.conditions, e.assignment))
			} else if (e.from == stmt) {
				newEdges = newEdges.plus(new Edge(newStmt, e.to, e.conditions, e.assignment))
			} else if (e.to == stmt) {
				newEdges = newEdges.plus(new Edge(e.from, newStmt, e.conditions, e.assignment))
			} else {
				newEdges = newEdges.plus(e)
			}
		}

		def entry =  stmt == entryNode ? newStmt : entryNode
		new ControlFlowGraph(algorithm, entry, lastNode, newNodes, newEdges)
	}

	def ControlFlowGraph merge(ControlFlowGraph graph, Block algorithm, Statement entryNode, Statement lastNode) {
		def newNodes = nodes
		graph.nodes.each { Statement stmt ->
			newNodes = newNodes.plus(stmt)
		}
		def newEdges = edges
		graph.edges.each { Edge e ->
			newEdges = newEdges.plus(e)
		}
		new ControlFlowGraph(algorithm, entryNode, lastNode, newNodes, newEdges)
	}

	def String toString() {
		return "("+nodes.toString()+","+edges.toString()+")"
	}

	def size() {
		return nodes.size()
	}

	def Set<Edge> outEdges(Statement stmt) {
		edges.findAll {  it.from == stmt }
	}

	def Set<Edge> inEdges(Statement stmt) {
		edges.findAll { it.to == stmt }
	}

	private Tuple2<Set<String>, Map<String, String>> representation() {
		NodeNaming n = new NodeNaming(algorithm)
		Set<String> node = nodes.collect { n.getName(it) }
		Map<String, String> edge = [:]
		edges.each { Edge e ->
			edge[e.getName(n)] = n.getName(e.to)
		}
		return new Tuple2<List<String>, Map<String, String>>(node, edge)
	}
}
