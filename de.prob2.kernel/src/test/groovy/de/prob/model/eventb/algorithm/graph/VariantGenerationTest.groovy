package de.prob.model.eventb.algorithm.graph

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.transform.VariantAssertion
import de.prob.model.eventb.algorithm.ast.transform.VariantPropagator
import de.prob.model.representation.ModelElementList

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

	def propagate(Closure cls) {
		Block b = new Block().make(cls)
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
		def expected = [assign4:[
				"2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant & while0_variant>0",
				"card(worklist)<while1_variant & while1_variant>0"
			], assign3:[
				"2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant & while0_variant>0",
				"card(worklist)<while1_variant & while1_variant>0"
			], if0:[
				"prj1(next) /: nullable & ran(prj2(next)) <: nullable => (2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant & while0_variant>0)",
				"card(worklist)<while1_variant & while1_variant>0",
				"not(prj1(next) /: nullable & ran(prj2(next)) <: nullable) => (2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant & while0_variant > 0)"
			], assign2:[
				"prj1(next) /: nullable & ran(prj2(next)) <: nullable => (2*(card(Symbols) - card(nullable\\/{prj1(next)}))+{TRUE |-> 1,FALSE |-> 0}(TRUE)<while0_variant & while0_variant>0)",
				"card(worklist \\ {next})<while1_variant & while1_variant>0",
				"not(prj1(next) /: nullable & ran(prj2(next)) <: nullable) => (2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(chng)<while0_variant & while0_variant>0)"
			], assign1:[
				"2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant & while0_variant > 0",
				"card(worklist) < while1_variant & while1_variant > 0"
			], while1:[
				"2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng) < while0_variant & while0_variant > 0"
			], assign0:[
				"2*(card(Symbols) - card(nullable))+{TRUE |-> 1,FALSE |-> 0}(FALSE)<while0_variant & while0_variant>0"
			], while0:[]]

		then:
		//println propagated
		propagated == expected

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
