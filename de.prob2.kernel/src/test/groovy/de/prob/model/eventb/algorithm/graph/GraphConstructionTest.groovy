package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block

public class GraphConstructionTest extends Specification {

	def AlgorithmGraph graph(Closure cls) {
		Block b = new Block().make(cls)
		AlgorithmToGraph g = new AlgorithmToGraph(b)
		return new AlgorithmGraph(g.getNode())
	}

	def EventInfo node(AlgorithmGraph graph, int index) {
		graph.nodes[index]
	}

	def conditions(AlgorithmGraph graph, int index) {
		EventInfo n = graph.nodes[index]
		n.conditions.collectEntries { pc, b ->
			[pc, b.conditions]
		}
	}

	def print(graph) {
		println "nodes: $graph"
		println "nodes->info: ${graph.nodeToInfoMapping}"
		println "nodes->pc: ${graph.nodeToPcMapping}\n"
	}

	def "empty is empty"() {
		when:
		def DEBUG = false
		def graph = graph({})

		then:
		if (DEBUG) print(graph)
		graph.nodes.isEmpty()
		graph.size() == 0
	}

	def "one assignment block has two nodes"() {
		when:
		def DEBUG = false
		def graph = graph({ Assign("x := 1", "y := 1") })

		then:
		if (DEBUG) print(graph)
		conditions(graph, 0) == [0: []]
		node(graph, 0).actions == [
			new Assignments(["x := 1", "y := 1"]),
			new Assignments(["pc := 1"])
		]
		conditions(graph, 1) == [1: []]
		node(graph, 1).actions.isEmpty()
		graph.size() == 2
	}

	def "one assert block is empty but has assertions"() {
		when:
		def DEBUG = false
		def graph = graph({ Assert("x = 1") })

		then:
		if (DEBUG) print(graph)
		graph.nodes.isEmpty()
		graph.size() == 0
	}

	def "an empty if has only one node"() {
		when:
		def DEBUG = false
		def graph = graph({
			If("x < 4") {
				Then {}
				Else {}
			}
		})

		then:
		if (DEBUG) print(graph)
		conditions(graph, 0) == [0: ["(x < 4) or (not(x < 4))"]]
		node(graph, 0).actions == []
	}

	def "an if with then has 2 nodes"() {
		when:
		def DEBUG = false
		def graph = graph({
			If("x < 4") {
				Then("x := 1")
				Else {}
			}
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		conditions(graph, 0) == [0: ["x < 4"]]
		node(graph, 0).actions == [
			new Assignments(["x := 1"]),
			new Assignments(["pc := 1"])
		]
		conditions(graph, 1) == [0:["not(x < 4)"], 1:[]]
		node(graph, 1).actions == []
	}

	def "an if with else has 3 nodes"() {
		when:
		def DEBUG = false
		def graph = graph({
			If("x < 4") {
				Then {}
				Else("x := 1")
			}
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		conditions(graph, 0) == [0: ["x < 4"], 1:[]]
		node(graph, 0).actions == []
		conditions(graph, 1) == [0:["not(x < 4)"]]
		node(graph, 1).actions == [
			new Assignments(["x := 1"]),
			new Assignments(["pc := 1"])
		]
	}

	def "an empty while has 2 nodes"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 4") {
			}
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		conditions(graph, 0) == [0: ["x < 4"]]
		node(graph, 0).actions == [new Assignments(["pc := 0"])]
		conditions(graph, 1) == [0: ["not(x < 4)"]]
		node(graph, 1).actions == []
	}

	def "a while with one stmt"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 4") { Assign("x := 2") }
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		conditions(graph,0) == [0: ["x < 4"]]
		node(graph, 0).actions == [
			new Assignments(["x := 2"]),
			new Assignments(["pc := 0"])
		]
		conditions(graph, 1) == [0: ["not(x < 4)"]]
		node(graph, 1).actions == []
	}

	/*
	 def "euclid"() {
	 when:
	 def graph = graph({
	 While("u /= 0") {
	 If ("u < v") { Then("u := v", "v := u") }
	 Assign("u := u - v")
	 }
	 Assert("u|->m|->n : IsGCD")
	 })
	 then:
	 if (DEBUG) print(graph)
	 node(graph, 0, Branch).condition == "u /= 0"
	 node(graph, 1, Branch).condition == "u < v"
	 node(graph, 2, Node).getStatements() == [
	 new Assignments(["u := v", "v := u"])
	 ]
	 node(graph, 3, Graft)
	 node(graph, 4, Node).getStatements() == [
	 new Assignments(["u := u - v"])
	 ]
	 node(graph, 5, Node).getStatements() == [
	 new Assertion("u|->m|->n : IsGCD")
	 ]
	 node(graph, 6, Nil)
	 edge(graph, 0, 1, "-- u /= 0 -->")
	 edge(graph, 0, 5, "-- not(u /= 0) -->")
	 edge(graph, 1, 2, "-- u < v -->")
	 edge(graph, 1, 3, "-- not(u < v) -->")
	 edge(graph, 2, 3, "-->")
	 edge(graph, 3, 4, "-->")
	 edge(graph, 4, 0, "-->")
	 edge(graph, 5, 6, "-->")
	 }*/

	def "optimized euclid"() {
		when:
		def DEBUG = true
		def b = new Block().make {
			While("u /= 0") {
				If ("u < v") { Then("u := v", "v := u") }
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		}
		def n = new GraphOptimizer(new AlgorithmToGraph(b).getNode())
		def graph = new AlgorithmGraph(n.getAlgorithm())

		then:
		if (DEBUG) print(graph)
		conditions(graph, 0) == [0: ["u /= 0", "u < v"]]
		node(graph, 0).actions == [
			new Assignments(["u := v", "v := u"]),
			new Assignments(["pc := 1"])
		]
		conditions(graph, 1) == [0: ["u /= 0", "not(u < v)"], 1:[]]
		node(graph, 1).actions == [
			new Assignments(["u := u - v"]),
			new Assignments(["pc := 0"])
		]
		conditions(graph, 2) == [0: ["not(u /= 0)"]]
		node(graph, 2).actions == []
	}
}
