package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

class PCCalculationWMergeOptTest extends Specification {

	def PCCalculator graph(Closure cls) {
		Block b = new Block().make(cls).finish()
		ControlFlowGraph g = new MergeConditionals().transform(new MergeAssignment().transform(new ControlFlowGraph(b)))
		//ControlFlowGraph g = new MergeAssignment().transform(new MergeConditionals().transform(new ControlFlowGraph(b)))
		return new PCCalculator(g)
	}

	def pcInfo(PCCalculator calc) {
		NodeNaming n = new NodeNaming(calc.graph.algorithm)
		calc.pcInformation.collectEntries { k,v ->
			[n.getName(k), v]
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
		pcInfo(graph) == [while0: 0, assign1: 1, assign2: 2, end_algorithm: 3]
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def graph = graph({
			While("l /= 1") {
				Assign("l := l / 2")
				Assign("r := r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(graph)
		pcInfo(graph) == [while0: 0, assign1: 1, if0: 2, end_algorithm: 3]
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
		pcInfo(graph) == [while0: 0, if3: 1, assign6: 2, end_algorithm: 3]
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
		pcInfo(graph) == [assign0: 0, assign1: 1, while0: 2, if0: 3, while1: 4, assign5: 5, end_algorithm: 6]
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
				Assign("y := y / 2")
				Assign("x := x / 2")
			}
			Assign("z := y + x")
		})

		then:
		if (DEBUG) print(graph)
		pcInfo(graph) == [while0: 0, while1: 1, assign2: 2, end_algorithm: 3]
	}
}
