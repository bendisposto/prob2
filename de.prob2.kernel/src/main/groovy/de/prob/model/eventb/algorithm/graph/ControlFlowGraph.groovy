package de.prob.model.eventb.algorithm.graph

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

class ControlFlowGraph {
	Map<Statement, Set<Assertion>> properties

	LinkedHashSet<Statement> nodes = new LinkedHashSet<Statement>()
	LinkedHashMap<Statement, Set<Edge>> outgoingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	LinkedHashMap<Statement, Set<Edge>> incomingEdges = new LinkedHashMap<Statement, Set<Edge>>()
	Map<Edge, List<Statement>> edgeMapping = [:]
	Map<While, List<Edge>> loopsForTermination = [:]
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
		if (w.variant) {
			loopsForTermination[w] = []
		}
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
		def e = addEdge(from, to, [])
		addTerminationLoop(e, to)
	}

	def addSpecialEdge(Call from, Statement to) {
		def e = addEdge(from, to, [])
		addTerminationLoop(e, to)
	}

	def addSpecialEdge(Skip from, Statement to) {
		def e = addEdge(from, to, [])
		addTerminationLoop(e, to)
	}

	def addSpecialEdge(Return from, Statement to) {
		addEdge(from, lastNode, [])
	}

	def addSpecialEdge(Assertion from, Statement to) {
		throw new IllegalArgumentException("Assertion is not allowed to be alone at the end of a statement!")
	}

	def addSpecialEdge(While from, Statement to) {
		def e = addEdge(from, to, [from.notCondition])
		addTerminationLoop(e, to)
	}

	def addSpecialEdge(If from, Statement to) {
		if (from.Then.statements.isEmpty()) {
			def e = addEdge(from, to, [from.condition])
			addTerminationLoop(e, to)
		} else {
			addSpecialEdge(from.Then.statements.last(), to)
		}
		if (from.Else.statements.isEmpty()) {
			def e = addEdge(from, to, [from.elseCondition])
			addTerminationLoop(e, to)
		} else {
			addSpecialEdge(from.Else.statements.last(), to)
		}
	}

	def addTerminationLoop(Edge edge, Statement to) {
		if (loopsForTermination[to] != null) {
			loopsForTermination[to].add(edge)
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

	def getEventName(Edge e) {
		List<Statement> statements = edgeMapping[e]
		if (statements.size() == 1 && statements[0] instanceof IAssignment) {
			assert e.conditions.isEmpty()
			return "${nodeMapping.getName(statements[0])}"
		}
		assert e.conditions.size() == statements.size()
		[statements, e.conditions].transpose().collect { l ->
			getEventName(l[0], l[1])
		}.iterator().join("_")
	}

	def String getEventName(While s, EventB condition) {
		def name = nodeMapping.getName(s)
		if (condition == s.condition) {
			return "enter_$name"
		}
		if (condition == s.notCondition) {
			return "exit_$name"
		}
		return "unknown_branch_$name"
	}

	def String getEventName(If s, EventB condition) {
		def name = nodeMapping.getName(s)
		if (condition == s.condition) {
			return "${name}_then"
		}
		if (condition == s.elseCondition) {
			return "${name}_else"
		}
		return "unknown_branch_$name"
	}
}
