package de.prob.model.eventb.algorithm.graph

import spock.lang.Specification
import de.prob.model.eventb.algorithm.ast.Block

class VariantGenerationTest extends Specification {
	def create(Closure cls) {
		Block b = new Block().make(cls)
		new VariantGenerator(new NodeNaming(b)).visit(b)
	}

	def nodeLoops(Closure cls) {
		Block b = new Block().make(cls)
		ControlFlowGraph g = new ControlFlowGraph(b)
		g.loopsForTermination.collectEntries { w, o ->
			[
				g.nodeMapping.getName(w),
				o.collect { g.getEventName(it) }
			]
		}
	}


	def "ll parsing algorithm"() {
		when:
		def var = create({
			While("chng = TRUE", variant: "2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng)") {
				Assign("chng := FALSE")
				While("worklist /= {}", invariant: "worklist <: G & next : G", variant: "card(worklist)") {
					// Assert("worklist /= {}")
					Assign("next :: worklist")
					Assign("worklist := worklist \\ {next}")
					If ("prj1(next) /: nullable & ran(prj2(next)) <: nullable") {
						Then {
							Assign("nullable := nullable \\/ {prj1(next)}")
							Assign("chng := TRUE")
						}
					}
				}
			}
		})

		then:
		var == [
			"while0_variant * while1_variant + while0_variant"
		]
	}

	def "ll parsing algorithm node loops"() {
		when:
		def loops = nodeLoops({
			While("chng = TRUE", variant: "2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng)") {
				Assign("chng := FALSE")
				While("worklist /= {}", invariant: "worklist <: G & next : G", variant: "card(worklist)") {
					// Assert("worklist /= {}")
					Assign("next :: worklist")
					Assign("worklist := worklist \\ {next}")
					If ("prj1(next) /: nullable & ran(prj2(next)) <: nullable") {
						Then {
							Assign("nullable := nullable \\/ {prj1(next)}")
							Assign("chng := TRUE")
						}
					}
				}
			}
		})

		then:
		loops == [
			while0: ["exit_while1"], while1: ["assign4", "if0_else"]]
	}

	def "two while loops after each other"() {
		when:
		def var = create({
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		var == [
			"while0_variant",
			"while1_variant"
		]
	}

	def "two while loops after each other (node loops)"() {
		when:
		def loops = nodeLoops({
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		loops == [
			while0:["assign0"],
			while1:["assign1"]]
	}

	def "in an if statement"() {
		when:
		def var = create({
			If("x < z") {
				Then {
					While("x < 10",variant: "x") { Assign("x := x - 1") }
					While("y < 10",variant: "y") { Assign("y := y - 1") }
				}
				Else {
					While("z < 10",variant: "z") { Assign("z := z - 2") }
				}
			}

		})

		then:
		var == [
			"while0_variant + while1_variant + while2_variant"
		]
	}

	def "in an if statement node loops"() {
		when:
		def loops = nodeLoops({
			If("x < z") {
				Then {
					While("x < 10",variant: "x") { Assign("x := x - 1") }
					While("y < 10",variant: "y") { Assign("y := y - 1") }
				}
				Else {
					While("z < 10",variant: "z") { Assign("z := z - 2") }
				}
			}

		})

		then:
		loops == [while0: ["assign0"],
			while1: ["assign1"],
			while2: ["assign2"]]
	}
}
