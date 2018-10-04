package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

import static de.prob.model.eventb.algorithm.graph.ControlFlowGraph.createSubgraph 

class CFGIfTest extends Specification {

	def "empty if"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", {}))

		then:
		g.representation().first == ["if0", "filler"] as Set
		g.representation().second == ["if0_then":"filler", "if0_else": "filler"]
	}

	def "if then"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", { Then("x := 1") }))

		then:
		g.representation().first == ["if0", "assign0", "filler"] as Set
		g.representation().second == ["if0_then":"assign0", "assign0": "filler", "if0_else": "filler"]
	}

	def "if else"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", { Else("x := 1") }))

		then:
		g.representation().first == ["if0", "assign0", "filler"] as Set
		g.representation().second == ["if0_then":"filler", "assign0": "filler", "if0_else": "assign0"]
	}

	def "if then with following assignment"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", { Then("x := 1") }).Assign("x := x + 1"))

		then:
		g.representation().first == ["if0", "assign0", "assign1"] as Set
		g.representation().second == ["if0_then": "assign0", "if0_else": "assign1", "assign0": "assign1"]
	}

	def "if else with following assignment"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", { Else("x := 1") }).Assign("x := x + 1"))

		then:
		g.representation().first == ["if0", "assign0", "assign1"] as Set
		g.representation().second == ["if0_then": "assign1", "if0_else": "assign0", "assign0": "assign1"]
	}

	def "if then else with following assignment"() {
		when:
		def g = createSubgraph(new Block().If("x > 1", {
			Then("x := 2")
			Else("x := 1")
		}).Assign("x := x + 1"))

		then:
		g.representation().first == ["if0", "assign0", "assign1", "assign2"] as Set
		g.representation().second == ["if0_then": "assign0", "if0_else": "assign1", "assign0": "assign2", "assign1": "assign2"]
	}

	def "nested ifs"() {
		when:
		def g = createSubgraph(new Block().If("a > 1", {
			Then {
				If("b > 1") { Then("z := z + 1") }
			}
			Else("z := 3")
		}))

		then:
		g.representation().first == ["if0", "if1", "assign0", "assign1", "filler"] as Set
		g.representation().second == [
			if0_then: "if1",
			if0_else: "assign1",
			if1_then: "assign0",
			if1_else: "filler",
			assign0: "filler",
			assign1:"filler",
		]
	}
}
