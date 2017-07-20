package de.prob.model.eventb.algorithm.graph

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

	/**
	 * Used in a subgraph to denote ambiguous exit nodes
	 */
	public final static Skip FILLER = new Skip()

	/**
	 * Used in a subgraph to denote the end of the algorithm
	 */
	public final static Skip END = new Skip()
	/**
	 * An empty control flow graph
	 */
	public final static ControlFlowGraph EMPTY = new ControlFlowGraph(new Block(), FILLER, FILLER, PersistentHashSet.emptySet(),  PersistentHashSet.emptySet())

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

	/**
	 * Calls {@link Block#ensureFinished()} to add end node if necessary.
	 * Calls addNode to recursively build graph
	 * @param algorithm for which a control flow graph is created
	 * @return control flow graph for algorithm
	 */
	def static ControlFlowGraph create(Block block) {
		Block algorithm = block.ensureFinished()
		assert !algorithm.statements.isEmpty()

		def lastStmt = algorithm.statements.last()
		def cfg = new ControlFlowGraph(algorithm, algorithm.statements.first(), algorithm.statements.last(), PersistentHashSet.emptySet(), PersistentHashSet.emptySet())
		cfg = cfg.addNode(algorithm.statements.first(), algorithm.statements.tail())
		return cfg.replaceNode(END, lastStmt)
	}

	/**
	 * If algorithm is empty, {@link ControlFlowGraph#EMPTY} will be returned.
	 * If the returned subgraph has an ambiguous exit node (i.e. multiple exit nodes from an if statement),
	 * these will be denoted with the {@link ControlFlowGraph#FILLER} node.
	 * The end of the algorithm will be denoted by the {@link ControlFlowGraph#END} node.
	 * All return statements will have this node as their exit node.
	 * @param algorithm representing the subgraph
	 * @return control flow graph created from algorithm
	 */
	def static ControlFlowGraph createSubgraph(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			def lastStmt = algorithm.statements.last()
			lastStmt = lastStmt instanceof Return ? END : lastStmt
			def cfg = new ControlFlowGraph(algorithm, algorithm.statements.first(), lastStmt, PersistentHashSet.emptySet(), PersistentHashSet.emptySet())
			cfg = cfg.addNode(algorithm.statements.first(), algorithm.statements.tail())
			return cfg
		}
		EMPTY
	}

	def ControlFlowGraph setEntryNode(Statement node) {
		return new ControlFlowGraph(algorithm, node, lastNode, nodes, edges)
	}

	def ControlFlowGraph setExitNode(Statement node) {
		return new ControlFlowGraph(algorithm, entryNode, node, nodes, edges)
	}

	def ControlFlowGraph setAlgorithm(Block algorithm) {
		return new ControlFlowGraph(algorithm, entryNode, lastNode, nodes, edges)
	}

	def ControlFlowGraph addEdge(Statement from, Statement to, PersistentVector<Tuple2<Statement, EventB>> conditions) {
		addEdge(new Edge(from, to, conditions))
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
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector()).setEntryNode(a)
	}

	def ControlFlowGraph addNode(Call a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), edges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, PersistentVector.emptyVector()).setEntryNode(a)
	}

	def ControlFlowGraph addNode(Return a, List<Statement> stmts) {
		addEdge(a, END, PersistentVector.emptyVector()).setEntryNode(a)
	}

	def ControlFlowGraph addNode(Skip a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return new ControlFlowGraph(algorithm, a, lastNode, nodes.plus(a), edges)
		}
		ControlFlowGraph cfg = addNode(stmts.first(), stmts.tail())
		cfg.addEdge(a, cfg.entryNode, lastNode, PersistentVector.emptyVector()).setEntryNode(a)
	}

	def ControlFlowGraph addNode(Assertion a, List<Statement> stmts) {
		assert !stmts.isEmpty() // assertions are mapped to the next statement, so an assertion before empty statements is incorrect
		addNode(stmts.first(), stmts.tail())
	}

	def PersistentVector<Tuple2<Statement,EventB>> newCondition(Statement s, EventB condition) {
		PersistentVector.emptyVector().plus(new Tuple2<Statement,EventB>(s,condition))
	}

	def addNode(While w, List<Statement> stmts) {
		if (w.block.statements.isEmpty()) {
			throw new IllegalArgumentException("While loops cannot be empty!")
		}
		ControlFlowGraph bodyG = createSubgraph(w.block)
		ControlFlowGraph g = bodyG.addEdge(w, bodyG.entryNode, newCondition(w, w.condition))
				.connect([bodyG.lastNode], w)

		ControlFlowGraph nextG = stmts.isEmpty() ? EMPTY : addNode(stmts.first(), stmts.tail())
		g = g.merge(nextG).addEdge(w, nextG.entryNode, newCondition(w, w.notCondition))
				.setEntryNode(w).setExitNode(nextG.lastNode).setAlgorithm(algorithm)
		g
	}

	def ControlFlowGraph addNode(If i, List<Statement> stmts) {
		ControlFlowGraph thenG = createSubgraph(i.Then)
		ControlFlowGraph elseG = createSubgraph(i.Else)
		ControlFlowGraph g = thenG.merge(elseG)
				.addEdge(i, thenG.entryNode, newCondition(i, i.condition))
				.addEdge(i, elseG.entryNode, newCondition(i, i.elseCondition))
				.setEntryNode(i)
				.setAlgorithm(algorithm)


		ControlFlowGraph nextG = stmts.isEmpty() ? EMPTY : addNode(stmts.first(), stmts.tail())
		g = g.merge(nextG).connect([
			thenG.lastNode,
			elseG.lastNode
		], nextG.entryNode).setExitNode(nextG.lastNode)
		g
	}

	def ControlFlowGraph connect(List<Statement> stmts, Statement to) {
		ControlFlowGraph g = this
		g = g.replaceNode(FILLER, to)
		stmts.each { Statement from ->
			if (from != FILLER && from != END) {
				g = g.addEdge(from, to, PersistentVector.emptyVector())
			}
		}
		g
	}

	def ControlFlowGraph connect(ControlFlowGraph nextG, Set<Statement> toConnect) {
		ControlFlowGraph g = merge(nextG)
		g = g.replaceNode(FILLER, nextG.entryNode)
		toConnect.each { Statement stmt ->
			g = g.addEdge(stmt, nextG.entryNode, PersistentVector.emptyVector())
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

	def ControlFlowGraph merge(ControlFlowGraph graph) {
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
		edges.findAll {  it.from == stmt } as Set
	}

	def Set<Edge> inEdges(Statement stmt) {
		edges.findAll {  it.to == stmt  } as Set
	}

	Tuple2<Set<String>, Map<String, String>> representation() {
		NodeNaming n = new NodeNaming(algorithm)
		Set<String> node = nodes.collect { n.getName(it) }
		Map<String, String> edge = [:]
		edges.each { Edge e ->
			edge[e.getName(n)] = n.getName(e.to)
		}
		return new Tuple2<List<String>, Map<String, String>>(node, edge)
	}

	public Map<While, Set<Edge>> loopsToWhile() {
		PCCalculator pcCalc = new PCCalculator(this)
		def loops = [:]
		def whiles = nodes.findAll { it instanceof While }
		whiles.each { w ->
			def pcW = pcCalc.pcInformation[w]
			def inEdges = inEdges(w)
			def edges = inEdges.findAll { e ->
				def pcE = pcCalc.pcInformation[e.from]
				pcE >= pcW
			}
			loops[w] = edges
		}
		loops
	}
}
