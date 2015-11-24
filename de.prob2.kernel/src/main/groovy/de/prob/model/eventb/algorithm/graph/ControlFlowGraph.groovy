package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.IProperty
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.transform.DeadCodeRemover
import de.prob.model.eventb.algorithm.ast.transform.AssertionExtractor

class ControlFlowGraph {
	Map<Statement, Set<Assertion>> properties

	LinkedHashSet<Statement> nodes = new LinkedHashSet<Statement>()
	LinkedHashMap<Statement, Set<Edge>> outgoingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	LinkedHashMap<Statement, Set<Edge>> incomingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	Map<Edge, List<Statement>> edgeMapping = [:]
	NodeNaming nodeMapping
	Statement entryNode

	Block algorithm
	Statement lastNode

	def ControlFlowGraph(Block b) {
		if (!b.statements.isEmpty()) {
			AssertionExtractor e = new AssertionExtractor()
			this.algorithm = e.transform(b.finish())
			lastNode = this.algorithm.statements.last()
			properties = e.properties
			nodeMapping = new NodeNaming(this.algorithm)

			if (!this.algorithm.statements.isEmpty()) {
				entryNode = addNode(this.algorithm.statements.first(), this.algorithm.statements.tail())
			}
		}
	}

	def addEdge(Statement from, Statement to, List<EventB> conditions) {
		nodes.add(from)
		nodes.add(to)
		Edge e = new Edge(from, to, conditions)
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

	def addNode(Assignment a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			nodes.add(a)
			return a
		}
		addEdge(a, addNode(stmts.first(), stmts.tail()), [])
		a
	}

	def addNode(Call a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			nodes.add(a)
			return a
		}
		addEdge(a, addNode(stmts.first(), stmts.tail()), [])
		a
	}

	def addNode(Return a, List<Statement> stmts) {
		if (stmts.isEmpty()) {
			nodes.add(a)
			return a
		}
		addEdge(a, lastNode, [])
		a
	}

	def addNode(Skip a, List<Statement> stmts) {
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
		addSpecialEdge(block.last(), w)

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
				addSpecialEdge(i.Then.statements.last(), nextN)
			}
		} else if (nextN) {
			addEdge(i, nextN, [i.condition])
		}

		if (!i.Else.statements.isEmpty()) {
			addEdge(i, addNode(i.Else.statements.first(), i.Else.statements.tail()), [i.elseCondition])
			if (nextN) {
				addSpecialEdge(i.Else.statements.last(), nextN)
			}
		} else if (nextN) {
			addEdge(i, nextN, [i.elseCondition])
		}
		i
	}

	def addSpecialEdge(Assignment from, Statement to) {
		addEdge(from, to, [])
	}

	def addSpecialEdge(Call from, Statement to) {
		addEdge(from, to, [])
	}

	def addSpecialEdge(Skip from, Statement to) {
		addEdge(from, to, [])
	}

	def addSpecialEdge(Return from, Statement to) {
		addEdge(from, lastNode, [])
	}

	def addSpecialEdge(Assertion from, Statement to) {
		throw new IllegalArgumentException("Assertion is not allowed to be alone at the end of a statement!")
	}

	def addSpecialEdge(While from, Statement to) {
		addEdge(from, to, [from.notCondition])
	}

	def addSpecialEdge(If from, Statement to) {
		if (from.Then.statements.isEmpty()) {
			addEdge(from, to, [from.condition])
		} else {
			addSpecialEdge(from.Then.statements.last(), to)
		}
		if (from.Else.statements.isEmpty()) {
			addEdge(from, to, [from.elseCondition])
		} else {
			addSpecialEdge(from.Else.statements.last(), to)
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
