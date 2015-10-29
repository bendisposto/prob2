package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.ast.Assignments;
import de.prob.model.eventb.algorithm.ast.Block;

public class PCCalculationWMergeOptTest extends Specification {

	def PCCalculator graph(Closure cls) {
		Block b = new Block().make(cls)
		return new PCCalculator(new GraphMerge().transform(new ControlFlowGraph(b)), true)
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
		pcInfo(graph) == [while0: 0, assign1: 1]
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
		pcInfo(graph) == [while0: 0, if0: 1]
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
		pcInfo(graph) == [while0: 0, if3: 1, assign6: 2, assign8: 3]
	}

	def "complicated while if 2"() {
		when:
		def DEBUG = false
		def graph = graph({
			Assign("y := 0", "x := 2")
			While("x = 2") {
				Assign("y := y + 1")
				If ("y > 10")  { Then("x := 3") }
			}
			While("x + y < 20") { Assign("x := x + 1", "y := y + 1") }
			Assert("x + y > 20")
		})

		then:
		if (DEBUG) print(graph)
		pcInfo(graph) == [assign0: 0, while0: 1, if0: 2, while1: 3]
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
		pcInfo(graph) == [while0: 0, while1: 1, assign3: 2]
	}
}
