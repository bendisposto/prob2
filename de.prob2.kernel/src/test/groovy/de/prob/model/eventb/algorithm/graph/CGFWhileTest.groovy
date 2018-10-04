package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.create
import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.createSubgraph

class CGFWhileTest extends Specification {

	def "two while loops after each other"() {
		when:
		def graph = create(new Block().make {
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		graph.representation().getFirst() == [
			"while0",
			"while1",
			"assign0",
			"assign1",
			"end_algorithm"
		] as Set
		graph.representation().getSecond() == [enter_while0: "assign0", assign0: "while0", exit_while0: "while1",
			enter_while1: "assign1", assign1: "while1", exit_while1: "end_algorithm"]
	}

	def "two while loops after each other in Edges"() {
		when:
		def graph = create(new Block().make {
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		graph.inEdges(graph.algorithm.statements[0]).size() == 1
		graph.inEdges(graph.algorithm.statements[1]).size() == 2
	}

	def "loop within loop"() {
		when:
		def g = create(new Block().make {
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
		g.representation().getFirst()  ==[
			"while0",
			"while1",
			"if0",
			"assign0",
			"assign1",
			"assign2",
			"end_algorithm"
		] as Set
		g.representation().getSecond() ==
				[enter_while0: "if0",if0_then:"while1",if0_else:"assign1",
					enter_while1: "assign0", assign0: "while1", exit_while1: "assign1",
					assign1:"while0","exit_while0": "assign2", "assign2": "end_algorithm"]
	}

	def "simple while"() {
		when:
		def g = createSubgraph(new Block().While("x < 10", { Assign("x := x + 1")}))

		then:
		g.representation().first == ["while0", "assign0", "filler"] as Set
		g.representation().second == [enter_while0: "assign0", assign0: "while0", exit_while0: "filler"]
	}

	def "while with an if"() {
		when:
		def g = createSubgraph(new Block().While("x < 10", {
			If("a > 1") {
				Then {
					If("b > 1") { Then("z := z + 1") }
				}
				Else("z := 3")
			}
		}))

		then:
		g.representation().first == ["while0", "if0", "if1", "assign0", "assign1", "filler"] as Set
		g.representation().second == [
			enter_while0: "if0",
			if0_then: "if1",
			if0_else: "assign1",
			if1_then: "assign0",
			if1_else: "while0",
			assign0: "while0",
			assign1: "while0",
			exit_while0: "filler",
		]
	}
}
