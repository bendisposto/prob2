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
	Map<Edge, List<Statement>> edgeMapping = [:]
	NodeNaming nodeMapping
	Statement entryNode

	Block algorithm

	def ControlFlowGraph(Block algorithm) {
		if (!algorithm.statements.isEmpty()) {
			// adding an assignments block to the end adds an extra event which goes into a deadlock.
			Block a = new Block(algorithm.statements.addElement(new Assignments()), algorithm.typeEnvironment)
			AssertionExtractor e = new AssertionExtractor()
			this.algorithm = e.transform(a)
			assertions = e.assertions
			nodeMapping = new NodeNaming(this.algorithm)

			if (!this.algorithm.statements.isEmpty()) {
				entryNode = addNode(this.algorithm.statements.first(), this.algorithm.statements.tail())
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
		edgeMapping[e] = [from]
		e
	}

	def addNode(Assignments a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			nodes.add(a)
			return a
		}
		addEdge(a, addNode(stmts.first(), stmts.tail()), [])
		a
	}

	def addNode(Assertion a, List<Statement> stmts) {
		assert !stmts.isEmpty() // assertions are mapped to the next statement, so an assertion before empty statements is incorrect
		addNode(stmts.first(), stmts.tail())
	}

	def addNode(While w, List<Statement> stmts) {
		List<Statement> block = w.block.statements
		if (block.isEmpty()) {
			throw new IllegalArgumentException("While loops cannot be null!")
		}
		addEdge(w, addNode(block.first(), block.tail()), [w.condition])
		addSpecialEdge(block.last(), w, true)

		if (!stmts.isEmpty()) {
			addEdge(w, addNode(stmts.first(), stmts.tail()), [w.notCondition])
		}
		w
	}

	def addNode(If i, List<Statement> stmts) {
		def nextN =	!stmts.isEmpty() ? addNode(stmts.first(), stmts.tail()) : null
		if (!i.Then.statements.isEmpty()) {
			addEdge(i, addNode(i.Then.statements.first(), i.Then.statements.tail()), [i.condition])
			if (nextN) {
				addSpecialEdge(i.Then.statements.last(), nextN, false)
			}
		} else if (nextN) {
			addEdge(i, nextN, [i.condition])
		}

		if (!i.Else.statements.isEmpty()) {
			addEdge(i, addNode(i.Else.statements.first(), i.Else.statements.tail()), [i.elseCondition])
			if (nextN) {
				addSpecialEdge(i.Else.statements.last(), nextN, false)
			}
		} else if (nextN) {
			addEdge(i, nextN, [i.elseCondition])
		}
		i
	}

	def addSpecialEdge(Assignments from, Statement to, boolean loopToWhile) {
		addEdge(from, to, [], loopToWhile)
	}

	def addSpecialEdge(Assertion from, Statement to, boolean loopToWhile) {
		throw new IllegalArgumentException("Assertion is not allowed to be alone at the end of a statement!")
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
