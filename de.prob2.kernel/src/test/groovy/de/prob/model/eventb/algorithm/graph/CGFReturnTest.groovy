package de.prob.model.eventb.algorithm.graph

import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.FILLER
import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.create
import spock.lang.Specification
import de.prob.model.eventb.algorithm.ast.Block

class CGFReturnTest extends Specification {
	def "correct return"() {
		when:
		def graph = create(new Block().make{
			If ("x = 5") {
				Then { Return("x") }
			}
			If ("y = 5") {
				Then { Return("y") }
			}
			Assign("z := 5")
			Return("z")
		})

		then:
		graph.size() == 7
		graph.representation().getFirst() == [
			"if0",
			"return0",
			"if1",
			"return1",
			"assign0",
			"return2",
			"end_algorithm"
		] as Set
		graph.representation().getSecond() == [if0_then: "return0", return0: "end_algorithm",
			if0_else: "if1", if1_then: "return1", return1: "end_algorithm", if1_else: "assign0",
			assign0: "return2", return2: "end_algorithm"]
	}
}
