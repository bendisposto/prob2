package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block

public class PCCalculationTest extends Specification {

	def PCCalculator graph(Closure cls) {
		Block b = new Block().make(cls)
		return new PCCalculator(new ControlFlowGraph(b))
	}

	def pcInfo(PCCalculator calc) {
		calc.pcInformation.collectEntries { k,v ->
			[
				calc.graph.nodeMapping.getName(k),
				v
			]
		}
	}

	def print(graph) {
		println pcInfo(graph)
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
		//		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0", "if0")
		//		graph.size() == 5
		//		assertions(graph, "assign1") == ["u > v"] as Set
		//		assertions(graph, "assign2") == ["u|->m|->n : IsGCD"] as Set
		//		edge(graph, "while0", "if0") == ["u /= 0"]
		//		edge(graph, "if0", "assign0") == ["u < v"]
		//		edge(graph, "if0", "assign1") == ["not(u < v)"]
		//		edge(graph, "assign0", "assign1") == []
		//		edge(graph, "assign1", "while0") == []
		//		edge(graph, "while0", "assign2") == ["not(u /= 0)"]
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
		//		graph.size() == 5
		//		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0", "if0")
		//		assertions(graph, "assign2") == ["product = m * n"] as Set
		//		edge(graph, "while0", "assign0") == ["l /= 1"]
		//		edge(graph, "assign0", "if0") == []
		//		edge(graph, "if0", "assign1") == ["l mod 2 /= 0"]
		//		edge(graph, "assign1", "while0") == []
		//		edge(graph, "if0", "while0") == ["not(l mod 2 /= 0)"]
		//		edge(graph, "while0", "assign2") == ["not(l /= 1)"]
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
		//		graph.size() == 14
		//		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3", "assign4",
		//				"assign5", "assign6", "assign7", "assign8", "while0", "if0", "if1", "if2", "if3")
		//		edge(graph, "while0","if0") == ["x : ODD"]
		//		edge(graph, "if0", "assign0") == ["x = 2"]
		//		edge(graph, "assign0","if3") == []
		//		edge(graph, "if0", "if1") == ["not(x = 2)"]
		//		edge(graph, "if1", "assign1") == ["x = 3"]
		//		edge(graph, "assign1", "if3") == []
		//		edge(graph, "if1", "if2") == ["not(x = 3)"]
		//		edge(graph, "if2", "assign2") == ["x = 4"]
		//		edge(graph, "assign2", "if3") == []
		//		edge(graph, "if2", "assign3") == ["not(x = 4)"]
		//		edge(graph, "assign3", "if3") == []
		//		edge(graph, "if3", "assign4") == ["y = 3"]
		//		edge(graph, "if3", "assign5") == ["not(y = 3)"]
		//		edge(graph, "assign4", "assign6") == []
		//		edge(graph, "assign5", "assign6") == []
		//		edge(graph, "assign6", "while0") == []
		//		edge(graph, "while0", "assign7") == ["not(x : ODD)"]
		//		edge(graph, "assign7", "assign8") == []
	}

	def "complicated while if 2"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("y := 0")
			Assign("x := 2")
			While("x = 2") {
				Assign("y := y + 1")
				If ("y > 10")  { Then("x := 3") }
			}
			While("x + y < 20") {
				Assign("x := x + 1")
				Assign("y := y + 1")
			}
			Assert("x + y > 20")
		})

		then:
		if (DEBUG) print(graph)
		//		graph.size() == 8
		//		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3",
		//				"assign4", "while0", "while1", "if0")
		//		assertions(graph, "assign4") == ["x + y > 20"] as Set
		//		edge(graph, "assign0", "while0") == []
		//		edge(graph, "while0", "assign1") == ["x = 2"]
		//		edge(graph, "assign1", "if0") == []
		//		edge(graph, "if0", "assign2") == ["y > 10"]
		//		edge(graph, "if0", "while0") == ["not(y > 10)"]
		//		edge(graph, "while0", "while1") == ["not(x = 2)"]
		//		edge(graph, "while1", "assign3") == ["x + y < 20"]
		//		edge(graph, "assign3", "while1") == []
		//		edge(graph, "while1", "assign4") == ["not(x + y < 20)"]
	}

	def "loop within loop"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 50") {
				If("y > x") {
					Then {
						While("x < y") { Assign("x := x + 1") }
					}
				}
				Assign("y := y / 2", "x := x / 2")
			}
			Assign("z := y + x")
		})

		then:
		if (DEBUG) print(graph)
		//		graph.size() == 7
		//		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3",
		//				"while0", "while1", "if0")
		//		edge(graph, "while0", "if0") == ["x < 50"]
		//		edge(graph, "if0", "while1") == ["y > x"]
		//		edge(graph, "while1", "assign0") == ["x < y"]
		//		edge(graph, "assign0", "while1") == []
		//		edge(graph, "while1", "assign1") == ["not(x < y)"]
		//		edge(graph, "if0", "assign1") == ["not(y > x)"]
		//		edge(graph, "assign1", "while0") == []
		//		edge(graph, "while0", "assign2") == ["not(x < 50)"]
		//		edge(graph, "assign2", "assign3") == []
	}
}
