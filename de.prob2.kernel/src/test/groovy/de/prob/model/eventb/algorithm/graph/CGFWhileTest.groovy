package de.prob.model.eventb.algorithm.graph

import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.FILLER
import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.create
import spock.lang.Specification
import de.prob.model.eventb.algorithm.ast.Block

class CGFWhileTest extends Specification {

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
		}.finish())

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
		def g = create(new Block().While("x < 10", { Assign("x := x + 1")}))

		then:
		g.representation() == [
			[
				"while0",
				"assign0",
				"filler"] as Set,
			[enter_while0:"assign0", assign0: "while0", exit_while0: "filler"]
		]
	}

	def "while with an if"() {
		when:
		def g = create(new Block().While("x < 10", {
			If("a > 1") {
				Then {
					If("b > 1") { Then("z := z + 1") }
				}
				Else("z := 3")
			}
		}))

		then:
		g.representation() == [
			[
				"while0",
				"if0",
				"if1",
				"assign0",
				"assign1",
				"filler"] as Set,
			[enter_while0:"if0", if0_then: "if1", if0_else: "assign1",
				if1_then: "assign0", if1_else: "while0",assign0: "while0", assign1: "while0", exit_while0: "filler" ]
		]
	}
}
