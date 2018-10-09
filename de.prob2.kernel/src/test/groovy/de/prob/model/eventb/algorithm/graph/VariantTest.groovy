package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.transform.AddLoopEvents
import de.prob.model.eventb.algorithm.ast.transform.VariantAssertion
import de.prob.model.eventb.algorithm.ast.transform.VariantPropagator
import de.prob.model.representation.ModelElementList

import spock.lang.Specification

class VariantTest extends Specification {

	def order(Closure cls) {
		Block b = new Block().make(cls)
		def v = new VariantOrdering()
		v.visit(b)
		def naming = new NodeNaming(b)
		v.ordering.collect { naming.getName(it) }
	}

	def propagate(Closure cls) {
		Block b = new AddLoopEvents(new AlgorithmGenerationOptions().loopEvent(true)).transform(new Block().make(cls))
		NodeNaming n = new NodeNaming(b)
		VariantPropagator ap = new VariantPropagator(new ModelElementList<Procedure>(), n)
		ap.traverse(b)
		ap.assertionMap.collectEntries { Statement stmt, List<VariantAssertion> v ->
			[
				n.getName(stmt),
				v.collect { it.toString() }
			]
		}
	}

	def "ll parsing algorithm order"() {
		when:
		def var = order({
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
		var == ["while0", "while1"]
	}

	def "formula equivalence"() {
		when:
		def f1 = new EventB("card(worklist)<while1_variant & while1_variant>0")
		def f2 = new EventB("card(worklist) < while1_variant & while1_variant > 0")
		def fuu = new FormulaUtil()

		then:
		fuu.getRodinFormula(f1) == fuu.getRodinFormula(f2)
	}

	def "ll parsing variant propagation"() {
		when:
		def propagated = propagate({
			While("chng = TRUE", variant: "2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng)") {
				// v0 & while1 & if0 => v0 & while1 & else0 => v0
				Assign("chng := FALSE")
				// v0 & while1 & if0 => v0 & while1 & else0 => v0
				While("worklist /= {}", invariant: "worklist <: G & next : G", variant: "card(worklist)") {
					// v0 & v1
					Assign("next :: worklist")
					// if0 => v0 & v1 & else0 => v0 & v1
					Assign("worklist := worklist \\ {next}")
					// if0 => v0 & v1 & else0 => v0 & v1
					If ("prj1(next) /: nullable & ran(prj2(next)) <: nullable") {
						Then {
							// v0 & v1
							Assign("nullable := nullable \\/ {prj1(next)}")
							// v0 & v1
							Assign("chng := TRUE")
						}
					}
				}
			}
		})
		def expected = [
			loop_to_while0:[
				"2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant"
			],
			loop_to_while1:[
				"2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant",
				"card(worklist) < while1_variant"
			],assign4:[
				"2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant",
				"card(worklist)<while1_variant"
			], assign3:[
				"2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant",
				"card(worklist)<while1_variant"
			], if0:[
				"prj1(next) /: nullable & ran(prj2(next)) <: nullable => (2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant)",
				"card(worklist)<while1_variant",
				"not(prj1(next) /: nullable & ran(prj2(next)) <: nullable) => (2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant)"
			], assign2:[
				"prj1(next)/:nullable&ran(prj2(next))<:nullable => (2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant)",
				"card(worklist \\ {next})<while1_variant",
				"not(prj1(next)/:nullable&ran(prj2(next))<:nullable) => (2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(chng)<while0_variant)"
			], assign1:[], while1:[
				"not(worklist /= {}) => (2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant)"
			], assign0:[
				"not worklist/={} => (2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(FALSE)<while0_variant)"
			], while0:[]]

		then:
		propagated == expected

	}

	def "two while loops after each other order"() {
		when:
		def var = order({
			While("x < 10",variant: "x") { Assign("x := x - 1") }
			While("y < 10",variant: "y") { Assign("y := y - 1") }
		})

		then:
		var == ["while0", "while1"]
	}

	def "in an if statement order"() {
		when:
		def var = order({
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
			"while0",
			"while1",
			"while2"
		]
	}

	def "while if order"() {
		when:
		def loops = order({
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
		loops == [
			"while0",
			"while1",
			"while2",
			"while3"
		]
	}
}
