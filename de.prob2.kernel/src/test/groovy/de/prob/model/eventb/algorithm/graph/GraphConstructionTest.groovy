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
			[
				pc,
				b.conditions.collect { it.getCode() }
			]
		}
	}

	def List<String> actions(AlgorithmGraph graph, int index) {
		EventInfo n = graph.nodes[index]
		def acts = []
		n.actions.each { Assignments a ->
			a.assignments.each { acts << it.getCode()}
		}
		acts
	}

	def Map<Integer, List<String>> assertions(AlgorithmGraph graph) {
		graph.assertions.collectEntries { pc, b ->
			[
				pc,
				b.collect { it.assertion.getCode() }
			]
		}
	}

	def print(graph) {
		println "nodes: $graph"
		println "nodes->info: ${graph.nodeToInfoMapping}"
		println "nodes->pc: ${graph.nodeToPcMapping}"
		println "assertions: ${graph.assertions}\n"
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
		actions(graph, 0) == [
			"x := 1",
			"y := 1",
			"pc := 1"
		]
		conditions(graph, 1) == [1: []]
		actions(graph, 1) == []
		graph.size() == 2
	}

	def "one assert block has two nodes and one assertion"() {
		when:
		def DEBUG = false
		def graph = graph({ Assert("x = 1") })

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		conditions(graph, 0) == [0: []]
		actions(graph, 0) == ["pc := 1"]
		conditions(graph, 1) == [1: []]
		actions(graph, 1) == []
		assertions(graph) == [0: ["x = 1"], 1:[]]
	}

	def "an assert in front of a statement"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("x := 2")
			Assert("x = 1")
			Assign("x := 3")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 3
		conditions(graph, 0) == [0: []]
		actions(graph, 0) == ["x := 2", "pc := 1"]
		conditions(graph, 1) == [1: []]
		actions(graph, 1) == ["x := 3", "pc := 2"]
		conditions(graph, 2) == [2: []]
		actions(graph, 2) == []
		assertions(graph) == [0: [], 1: ["x = 1"], 2:[]]
	}

	def "an assert before and after a while"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 4
		conditions(graph, 0) == [0: []]
		actions(graph, 0) == ["x := 1", "pc := 1"]
		conditions(graph, 1) == [1: ["x < 10"]]
		actions(graph, 1) == ["x := x + 1", "pc := 1"]
		conditions(graph, 2) == [1: ["not(x < 10)"]]
		actions(graph, 2) == ["pc := 2"]
		conditions(graph, 3) == [2: []]
		actions(graph, 3) == []
		assertions(graph) == [0: [], 1: ["x = 1"], 2: ["x >= 10"]]
	}

	def "an assert in between whiles"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 5
		conditions(graph, 0) == [0: []]
		actions(graph, 0) == ["x := 1", "pc := 1"]
		conditions(graph, 1) == [1: ["x < 10"]]
		actions(graph, 1) == ["x := x + 1", "pc := 1"]
		conditions(graph, 2) == [1: ["not(x < 10)", "x > 0"], 2: ["x > 0"]]
		actions(graph, 2) == ["x := x - 1", "pc := 2"]
		conditions(graph, 3) == [1: ["not(x < 10)", "not(x > 0)"], 2:["not(x > 0)"]]
		actions(graph, 3) == ["pc := 3"]
		conditions(graph, 4) == [3: []]
		actions(graph, 4) == []
		assertions(graph) == [0: [], 1:["x = 1"], 2:["x >= 10"], 3:["x = 0"]]
	}

	def "an assert between ifs"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("x := 1")
			Assert("x > 0")
			If("x > 0") {
				Then("x := 0 - x")
				Else("x := x - 1")
			}
			Assert("x < 0")
			If("x < 0") {
				Then("x := 0 - x")
				Else("x := x + 1")
			}
			Assert("x > 0")
		})

		then:
		if (DEBUG) print(graph)
		conditions(graph, 0) == [0: []]
		actions(graph, 0) == ["x := 1", "pc := 1"]
		conditions(graph, 1) == [1: ["x > 0"]]
		actions(graph, 1) == ["x := 0 - x", "pc := 2"]
		conditions(graph, 2) == [2: ["x < 0"]]
		actions(graph, 2) == ["x := 0 - x", "pc := 3"]
		conditions(graph, 3) == [3: []]
		actions(graph, 3) == ["pc := 4"]
		conditions(graph, 4) == [4: []]
	}

	def "two decrementers"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
			While("y > 0") { Assign("y := y - 1") }
			Assert("y = 0")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 4
		conditions(graph, 0) == [0: ["x > 0"]]
		actions(graph, 0) == ["x := x - 1", "pc := 0"]
		conditions(graph, 1) == [0: ["not(x > 0)", "y > 0"], 1: ["y > 0"]]
		actions(graph, 1) == ["y := y - 1", "pc := 1"]
		conditions(graph, 2) == [0: ["not(x > 0)", "not(y > 0)"], 1:["not(y > 0)"]]
		actions(graph, 2) == ["pc := 2"]
		conditions(graph, 3) == [2: []]
		actions(graph, 3) == []
		assertions(graph) == [0: [], 1:["x = 0"], 2:["y = 0"]]
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
		actions(graph, 0) == []
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
		actions(graph, 0) == ["x := 1", "pc := 1"]
		conditions(graph, 1) == [0:["not(x < 4)"], 1:[]]
		actions(graph, 1) == []
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
		actions(graph, 0) == []
		conditions(graph, 1) == [0:["not(x < 4)"]]
		actions(graph, 1) == ["x := 1", "pc := 1"]
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
		actions(graph, 0) == ["pc := 0"]
		conditions(graph, 1) == [0: ["not(x < 4)"]]
		actions(graph, 1) == []
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
		actions(graph, 0) == ["x := 2", "pc := 0"]
		conditions(graph, 1) == [0: ["not(x < 4)"]]
		actions(graph, 1) == []
	}

	def "optimized euclid"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("u /= 0") {
				If ("u < v") { Then("u := v", "v := u") }
				Assert("u > v")
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		if (DEBUG) print(graph)
		assertions(graph) == [0:[], 1: ["u > v"], 2: ["u|->m|->n : IsGCD"]]
		conditions(graph, 0) == [0: ["u /= 0", "u < v"]]
		actions(graph, 0) == [
			"u := v",
			"v := u",
			"pc := 1"
		]
		conditions(graph, 1) == [0: ["u /= 0", "not(u < v)"], 1:[]]
		actions(graph, 1) == ["u := u - v", "pc := 0"]
		conditions(graph, 2) == [0: ["not(u /= 0)"]]
		actions(graph, 2) == ["pc := 2"]
		conditions(graph, 3) == [2: []]
		actions(graph, 3) == []
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def graph = graph({
			While("l /= 1") {
				Assign("l := l / 2", "r := r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 5
		assertions(graph) == [0: [], 1: [], 2:["product = m * n"]]
		conditions(graph, 0) == [0: ["l /= 1"]]
		actions(graph, 0) == [
			"l := l / 2",
			"r := r * 2",
			"pc := 1"
		]
		conditions(graph, 1) == [1: ["l mod 2 /= 0"]]
		actions(graph, 1) == [
			"product := product + r",
			"pc := 0"
		]
		conditions(graph, 2) == [1: ["not(l mod 2 /= 0)"]]
		actions(graph, 2) == ["pc := 0"]
		conditions(graph, 3) == [0: ["not(l /= 1)"]]
		actions(graph, 3) == ["pc := 2"]
		conditions(graph, 4) == [2: []]
		actions(graph, 4) == []
	}

	def "complicated while if"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x : ODD") {
				If ("x = 2") {
					Then("x := x + 1")
					Else {
						If ("x = 3") {
							Then("x := x + 2")
							Else {
								If("x = 4") {
									Then("x := x + 3")
									Else("x := x - 5")
								}
							}
						}
					}
				}
				If ("y = 3") {
					Then("x := y + 2")
					Else("x := y + 3")
				}
				Assign("x := y - 2")
			}
			Assign("z := x + y")
		})

		then:
		if (DEBUG) print(graph)
	}

	//	def "loop within loop"() {
	//		when:
	//		def DEBUG = false
	//		def graph = graph({
	//			While("x < 50") {
	//				If("y > x") {
	//					Then {
	//						While("x < y") { Assign("x := x + 1") }
	//					}
	//				}
	//				Assign("y := y / 2", "x := x / 2")
	//			}
	//			Assign("z := y + x")
	//		})
	//
	//		then:
	//		if (DEBUG) print(graph)
	//	}
}
