package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.transform.AddLoopEvents

import spock.lang.Specification

class LoopToWhileTest extends Specification {
	def nodeLoops(Closure cls) {
		Block b = new Block().make(cls)
		ControlFlowGraph g = new ControlFlowGraph(b)
		def n = new NodeNaming(b)
		g.loopsToWhile().collectEntries { w, e ->
			[
				n.getName(w),
				e.collect { it.getName(n) } as Set
			]
		}
	}

	def mergeLoops(Closure cls) {
		Block b = new AddLoopEvents(new AlgorithmGenerationOptions().loopEvent(true)).transform(new Block().make(cls))
		ControlFlowGraph g = new MergeConditionals().transform(new ControlFlowGraph(b))
		def n = new NodeNaming(b)
		g.loopsToWhile().collectEntries { w, e ->
			[
				n.getName(w),
				e.collect { it.getName(n) } as Set
			]
		}
	}

	def "two while loops after each other (node loops)"() {
		when:
		def loops = nodeLoops({
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		loops == [
			while0:["assign0"] as Set,
			while1:["assign1"] as Set]
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
			while0: (["exit_while1"] as Set), while1: (["assign4", "if0_else"] as Set)]
	}

	def "ll parsing algorithm node loops graph merge"() {
		when:
		def loops = mergeLoops({
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
			while0: ["loop_to_while0"] as Set, while1: ["loop_to_while1"] as Set]
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
		loops == [while0: ["assign0"] as Set,
			while1: ["assign1"] as Set,
			while2: ["assign2"] as Set]
	}

	def "while if merge"() {
		when:
		def loops = mergeLoops({
			While("x < z", variant: "x") {
				If("x /= y") {
					Then {
						While("x < 10",variant: "x") { Assign("x := x - 1") }
						While("y < 10",variant: "y") { Assign("y := y - 1") }
					}
					Else {
						While("z < 10",variant: "z") { Assign("z := z - 2") }
					}
				}
			}

		})

		then:
		loops == [while1: ["loop_to_while1"] as Set,
			while2: ["loop_to_while2"] as Set,
			while3: ["loop_to_while3"] as Set,
			while0: ["loop_to_while0"] as Set]
	}
}
