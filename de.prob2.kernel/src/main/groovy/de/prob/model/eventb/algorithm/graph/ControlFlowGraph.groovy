package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class ControlFlowGraph {
	Map<Statement, Set<Assertion>> assertions

	LinkedHashSet<Statement> nodes = new LinkedHashSet<Statement>()
	LinkedHashMap<Statement, Set<Edge>> outgoingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	LinkedHashMap<Statement, Set<Edge>> incomingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	NodeNaming nodeMapping
	Statement entryNode

	Block algorithm

	def ControlFlowGraph(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			// adding an assignments block to the end adds an extra event which goes into a deadlock.
			Block a = new Block(algorithm.statements.addElement(new Assignments()), algorithm.typeEnvironment)
			this.algorithm = a
			AssertionExtractor e = new AssertionExtractor(a)
			assertions = e.assertions
			nodeMapping = new NodeNaming(e.algorithm)

			if (!e.algorithm.statements.isEmpty()) {
				entryNode = e.algorithm.statements.first()
				addNode(entryNode, e.algorithm.statements.tail())
			}
		}
	}

	def addEdge(Statement from, Statement to, List<EventB> conditions, boolean loopToWhile=false) {
		nodes.add(from)
		nodes.add(to)
		Edge e = new Edge(from, to, conditions, loopToWhile)
		if (!outgoingEdges[from]) {
			outgoingEdges[from] = new LinkedHashSet<Edge>()
		}
		if (!incomingEdges[to]) {
			incomingEdges[to] = new LinkedHashSet<Edge>()
		}
		outgoingEdges[from].add(e)
		incomingEdges[to].add(e)
	}

	def addNode(Assignments a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			nodes.add(a)
			return
		}
		addEdge(a, stmts.first(), [])
		addNode(stmts.first(), stmts.tail())
	}

	def addNode(While w, List<Statement> stmts) {
		List<Statement> block = w.block.statements
		if (block.isEmpty()) {
			throw new IllegalArgumentException("While loops cannot be null!")
		}
		addEdge(w, block.first(), [w.condition])
		addNode(block.first(), block.tail())
		addSpecialEdge(block.last(), w, true)

		if (!stmts.isEmpty()) {
			addEdge(w, stmts.first(), [w.notCondition])
			addNode(stmts.first(), stmts.tail())
		}
	}

	def addSpecialEdge(Assignments from, Statement to, boolean loopToWhile) {
		addEdge(from, to, [], loopToWhile)
	}

	def addSpecialEdge(While from, Statement to, boolean loopToWhile) {
		addEdge(from, to, [from.notCondition], loopToWhile)
	}

	def addSpecialEdge(If from, Statement to, boolean loopToWhile) {
		if (from.Then.statements.isEmpty()) {
			addEdge(from, to, [from.condition], loopToWhile)
		} else {
			addSpecialEdge(from.Then.statements.last(), to, loopToWhile)
		}
		if (from.Else.statements.isEmpty()) {
			addEdge(from, to, [from.elseCondition], loopToWhile)
		} else {
			addSpecialEdge(from.Else.statements.last(), to, loopToWhile)
		}
	}

	def addNode(If i, List<Statement> stmts) {
		if (!i.Then.statements.isEmpty()) {
			addEdge(i, i.Then.statements.first(), [i.condition])
			addNode(i.Then.statements.first(), i.Then.statements.tail())
			if (!stmts.isEmpty()) {
				addSpecialEdge(i.Then.statements.last(), stmts.first(), false)
			}
		} else if (!stmts.isEmpty()) {
			addEdge(i, stmts.first(), [i.condition])
		}

		if (!i.Else.statements.isEmpty()) {
			addEdge(i, i.Else.statements.first(), [i.elseCondition])
			addNode(i.Else.statements.first(), i.Else.statements.tail())
			if (!stmts.isEmpty()) {
				addSpecialEdge(i.Else.statements.last(), stmts.first(), false)
			}
		} else if (!stmts.isEmpty()) {
			addEdge(i, stmts.first(), [i.elseCondition])
		}

		if (!stmts.isEmpty()) {
			addNode(stmts.first(), stmts.tail())
		}
	}

	def size() {
		return nodes.size()
	}

	def getNode(String name) {
		return nodeMapping[name]
	}

	def outEdges(Statement stmt) {
		outgoingEdges[stmt] ?: []
	}

	def inEdges(Statement stmt) {
		incomingEdges[stmt] ?: []
	}
}
