package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.ast.Assignment;
import de.prob.model.eventb.algorithm.ast.Block;

public class CGGConstructionTest extends Specification {

	def ControlFlowGraph graph(Closure cls) {
		Block b = new Block().make(cls)
		return new ControlFlowGraph(b)
	}

	def nodes(ControlFlowGraph g, String... names) {
		names.collect { g.getNode(it) } as Set
	}

	def edge(ControlFlowGraph g, String from, String to) {
		assert g.getNode(from)
		Edge e = g.outgoingEdges[g.getNode(from)].find {
			it.to == g.getNode(to)
		}
		e ? e.conditions.collect { it.getCode() } : null
	}

	def edges(ControlFlowGraph g, String from, String to) {
		assert g.getNode(from)
		g.outgoingEdges[g.getNode(from)].findAll {
			it.to == g.getNode(to)
		}.collect {
			it.conditions.collect { it.getCode()
			} } as Set
	}

	def assertions(ControlFlowGraph g, String at) {
		g.properties[g.getNode(at)].collect {
			it.getAssertion().getCode()
		} as Set
	}

	def print(graph) {
		println "nodes: ${graph.nodes}"
		println "naming: ${graph.nodeMapping.nodes}"
		println "incoming: ${graph.incomingEdges}"
		println "outgoing: ${graph.outgoingEdges}"
		println "properties: ${graph.properties}\n"
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
		def graph = graph({ Assign("x := 1") })

		then:
		if (DEBUG) print(graph)
		graph.size() == 2
		graph.nodes == nodes(graph, "assign0", "assign1")
		edge(graph, "assign0", "assign1") == []
	}

	def "one assert block has one node and one assertion"() {
		when:
		def DEBUG = false
		def graph = graph({ Assert("x = 1") })

		then:
		if (DEBUG) print(graph)
		graph.size() == 1
		graph.nodes == nodes(graph, "assign0")
		assertions(graph, "assign0") == ["x = 1"] as Set
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
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2")
		graph.size() == 3
		assertions(graph, "assign1") == ["x = 1"] as Set
		edge(graph, "assign0", "assign1") == []
		edge(graph, "assign1", "assign2") == []
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
		assertions(graph, "while0") == ["x = 1"] as Set
		assertions(graph, "assign2") == ["x >= 10"] as Set
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0")
		edge(graph, "assign0", "while0") == []
		edge(graph, "while0", "assign1") == ["x < 10"]
		edge(graph, "assign1", "while0") == []
		edge(graph, "while0", "assign2") == ["not(x < 10)"]
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
		graph.size() == 6
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3", "while0", "while1")
		assertions(graph, "while0") == ["x = 1"] as Set
		assertions(graph, "while1") == ["x >= 10"] as Set
		assertions(graph, "assign3") == ["x = 0"] as Set
		edge(graph, "assign0", "while0") == []
		edge(graph, "while0", "assign1") == ["x < 10"]
		edge(graph, "assign1", "while0") == []
		edge(graph, "while0", "while1") == ["not(x < 10)"]
		edge(graph, "while1", "assign2") == ["x > 0"]
		edge(graph, "assign2", "while1") == []
		edge(graph, "while1", "assign3") == ["not(x > 0)"]
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
		graph.size() == 8
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3", "assign4", "assign5", "if0", "if1")
		assertions(graph, "if0") == ["x > 0"] as Set
		assertions(graph, "if1") == ["x < 0"] as Set
		assertions(graph, "assign5") == ["x > 0"] as Set
		edge(graph, "assign0","if0") == []
		edge(graph, "if0", "assign1") == ["x > 0"]
		edge(graph, "if0", "assign2") == ["not(x > 0)"]
		edge(graph, "assign1", "if1") == []
		edge(graph, "assign2", "if1") == []
		edge(graph, "if1", "assign3") == ["x < 0"]
		edge(graph, "if1", "assign4") == ["not(x < 0)"]
		edge(graph, "assign3", "assign5") == []
		edge(graph, "assign4", "assign5") == []
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
		graph.size() == 5
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0", "while1")
		edge(graph, "while0", "assign0") == ["x > 0"]
		edge(graph, "assign0", "while0") == []
		edge(graph, "while0", "while1") == ["not(x > 0)"]
		edge(graph, "while1", "assign1") == ["y > 0"]
		edge(graph, "assign1", "while1") == []
		edge(graph, "while1", "assign2") == ["not(y > 0)"]
	}

	def "an empty if has two nodes"() {
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
		graph.size() == 2
		graph.nodes == nodes(graph, "assign0", "if0")
		edges(graph, "if0", "assign0") == [["x < 4"], ["not(x < 4)"]] as Set
	}

	def "an if with then has 3 nodes"() {
		when:
		def DEBUG = false
		def graph = graph({
			If("x < 4") { Then("x := 1") }
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 3
		graph.nodes == nodes(graph, "assign0", "assign1", "if0")
		edge(graph, "if0", "assign0") == ["x < 4"]
		edge(graph, "assign0", "assign1") == []
		edge(graph, "if0", "assign1") == ["not(x < 4)"]
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
		graph.size() == 3
		graph.nodes == nodes(graph, "assign0", "assign1", "if0")
		edge(graph, "if0", "assign1") == ["x < 4"]
		edge(graph, "assign0", "assign1") == []
		edge(graph, "if0", "assign0") == ["not(x < 4)"]
	}

	def "cannot create empty while"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 4") {
			}
		})

		then:
		thrown IllegalArgumentException
	}

	def "a while with one stmt"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 4") { Assign("x := 2") }
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 3
		graph.nodes == nodes(graph, "assign0", "assign1", "while0")
		edge(graph, "while0", "assign0") == ["x < 4"]
		edge(graph, "assign0", "while0") == []
		edge(graph, "while0", "assign1") == ["not(x < 4)"]
	}

	def "optimized euclid"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("u /= 0") {
				If ("u < v") { Then("u,v := v,u") }
				Assert("u > v")
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		if (DEBUG) print(graph)
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0", "if0")
		graph.size() == 5
		assertions(graph, "assign1") == ["u > v"] as Set
		assertions(graph, "assign2") == ["u|->m|->n : IsGCD"] as Set
		edge(graph, "while0", "if0") == ["u /= 0"]
		edge(graph, "if0", "assign0") == ["u < v"]
		edge(graph, "if0", "assign1") == ["not(u < v)"]
		edge(graph, "assign0", "assign1") == []
		edge(graph, "assign1", "while0") == []
		edge(graph, "while0", "assign2") == ["not(u /= 0)"]
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def graph = graph({
			While("l /= 1") {
				Assign("l,r := l / 2, r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 5
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "while0", "if0")
		assertions(graph, "assign2") == ["product = m * n"] as Set
		edge(graph, "while0", "assign0") == ["l /= 1"]
		edge(graph, "assign0", "if0") == []
		edge(graph, "if0", "assign1") == ["l mod 2 /= 0"]
		edge(graph, "assign1", "while0") == []
		edge(graph, "if0", "while0") == ["not(l mod 2 /= 0)"]
		edge(graph, "while0", "assign2") == ["not(l /= 1)"]
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
		graph.size() == 14
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3", "assign4",
				"assign5", "assign6", "assign7", "assign8", "while0", "if0", "if1", "if2", "if3")
		edge(graph, "while0","if0") == ["x : ODD"]
		edge(graph, "if0", "assign0") == ["x = 2"]
		edge(graph, "assign0","if3") == []
		edge(graph, "if0", "if1") == ["not(x = 2)"]
		edge(graph, "if1", "assign1") == ["x = 3"]
		edge(graph, "assign1", "if3") == []
		edge(graph, "if1", "if2") == ["not(x = 3)"]
		edge(graph, "if2", "assign2") == ["x = 4"]
		edge(graph, "assign2", "if3") == []
		edge(graph, "if2", "assign3") == ["not(x = 4)"]
		edge(graph, "assign3", "if3") == []
		edge(graph, "if3", "assign4") == ["y = 3"]
		edge(graph, "if3", "assign5") == ["not(y = 3)"]
		edge(graph, "assign4", "assign6") == []
		edge(graph, "assign5", "assign6") == []
		edge(graph, "assign6", "while0") == []
		edge(graph, "while0", "assign7") == ["not(x : ODD)"]
		edge(graph, "assign7", "assign8") == []
	}

	def "complicated while if 2"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("y,x := 0,2")
			While("x = 2") {
				Assign("y := y + 1")
				If ("y > 10")  { Then("x := 3") }
			}
			While("x + y < 20") { Assign("x,y := x + 1,y+1") }
			Assert("x + y > 20")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 8
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3",
				"assign4", "while0", "while1", "if0")
		assertions(graph, "assign4") == ["x + y > 20"] as Set
		edge(graph, "assign0", "while0") == []
		edge(graph, "while0", "assign1") == ["x = 2"]
		edge(graph, "assign1", "if0") == []
		edge(graph, "if0", "assign2") == ["y > 10"]
		edge(graph, "if0", "while0") == ["not(y > 10)"]
		edge(graph, "while0", "while1") == ["not(x = 2)"]
		edge(graph, "while1", "assign3") == ["x + y < 20"]
		edge(graph, "assign3", "while1") == []
		edge(graph, "while1", "assign4") == ["not(x + y < 20)"]
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
				Assign("y,x := y / 2, x / 2")
			}
			Assign("z := y + x")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 7
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3",
				"while0", "while1", "if0")
		edge(graph, "while0", "if0") == ["x < 50"]
		edge(graph, "if0", "while1") == ["y > x"]
		edge(graph, "while1", "assign0") == ["x < y"]
		edge(graph, "assign0", "while1") == []
		edge(graph, "while1", "assign1") == ["not(x < y)"]
		edge(graph, "if0", "assign1") == ["not(y > x)"]
		edge(graph, "assign1", "while0") == []
		edge(graph, "while0", "assign2") == ["not(x < 50)"]
		edge(graph, "assign2", "assign3") == []
	}

	def "loopity loop loop loop"() {
		when:
		def DEBUG = false
		def graph = graph({
			While("x < 50") {
				If("y > x") {
					Then {
						While("x < y") { Assign("x := x + 1") }
					}
				}
			}
			While("z < 50") {
				If ("z < 0") { Then("z := 0 - z") }
				Assign("z := z + 1")
			}
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 9
		graph.nodes == nodes(graph, "assign0", "assign1", "assign2", "assign3",
				"while0", "while1", "while2", "if0", "if1")
		edge(graph, "while0", "if0") == ["x < 50"]
		edge(graph, "if0", "while1") == ["y > x"]
		edge(graph, "while1", "assign0") == ["x < y"]
		edge(graph, "assign0", "while1") == []
		edge(graph, "while1", "while0") == ["not(x < y)"]
		edge(graph, "if0", "while0") == ["not(y > x)"]
		edge(graph, "while0", "while2") == ["not(x < 50)"]
		edge(graph, "while2", "if1") == ["z < 50"]
		edge(graph, "if1", "assign1") == ["z < 0"]
		edge(graph, "assign1", "assign2") == []
		edge(graph, "if1", "assign2") == ["not(z < 0)"]
		edge(graph, "assign2", "while2") == []
		edge(graph, "while2", "assign3") == ["not(z < 50)"]
	}

	def "correct return"() {
		when:
		def DEBUG = false
		def graph = graph({
			If ("x = 5") {
				Then { Return("x") }
			}
			If ("y = 5") {
				Then { Return("x") }
			}
			Assign("z := 5")
			Return("z")
		})

		then:
		if (DEBUG) print(graph)
		graph.size() == 7
		graph.nodes == nodes(graph, "if0", "return0", "if1", "return1",
				"assign0", "return2", "assign1")
		edge(graph, "if0", "return0") == ["x = 5"]
		edge(graph, "return0", "assign1") == []
		edge(graph, "if0", "if1") == ["not(x = 5)"]
		edge(graph, "if1", "return1") == ["y = 5"]
		edge(graph, "if1", "assign0") == ["not(y = 5)"]
		edge(graph, "return1", "assign1") == []
		edge(graph, "assign0", "return2") == []
		edge(graph, "return2", "assign1") == []
	}
}
