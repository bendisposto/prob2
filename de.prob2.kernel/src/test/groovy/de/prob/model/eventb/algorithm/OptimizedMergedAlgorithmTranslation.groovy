package de.prob.model.eventb.algorithm

import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBInvariant
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier

import spock.lang.Specification

class OptimizedMergedAlgorithmTranslation extends Specification {
	private MachineModifier mm

	def setup() {
		final m = new EventBMachine("algorithm")
		mm = new MachineModifier(m)
	}

	def translate(MachineModifier mm) {
		ModelModifier modelM = new ModelModifier().addMachine(mm.getMachine())
		String name = mm.getMachine().getName()
		modelM = new AlgorithmTranslator(modelM.getModel(), new AlgorithmGenerationOptions().optimize(true).mergeBranches(true)).runTranslation(modelM, name)
		modelM.getModel().getMachine(name)
	}

	def guards(Event evt) {
		evt.guards.collect { it.getPredicate().getCode() }
	}

	def actions(Event evt) {
		evt.actions.collect { it.getCode().getCode() }
	}

	def inv(EventBInvariant i) {
		i.getPredicate().getCode()
	}

	def "translate an if without an else"() {
		when:
		def m = translate(mm.algorithm {
			If("x < 0") { Then("x := 0") }
		})

		then:
		def e = m.events
		e.if0_then != null
		guards(e.if0_then) == ["pc = 0", "x < 0"]
		actions(e.if0_then) == ["x := 0", "pc := 1"]

		e.if0_else != null
		guards(e.if0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.if0_else) == ["pc := 1"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 1"]
		actions(e.end_algorithm) == []
	}

	def "translate an if with an else"() {
		when:
		def m = translate(mm.algorithm {
			If("x < 0") {
				Then("x := 0")
				Else("x := 2")
			}
		})

		then:
		def e = m.events
		e.if0_then != null
		guards(e.if0_then) == ["pc = 0", "x < 0"]
		actions(e.if0_then) == ["x := 0", "pc := 1"]

		e.if0_else != null
		guards(e.if0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.if0_else) == ["x := 2", "pc := 1"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 1"]
	}

	def "translate a while loop"() {
		when:
		def m = translate(mm.algorithm {
			While("x < 0") { Assign("x := 0") }
		})

		then:
		def e = m.events
		e.enter_while0 != null
		guards(e.enter_while0) == ["pc = 0", "x < 0"]
		actions(e.enter_while0) == ["x := 0", "pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 0)"]
		actions(e.exit_while0) == ["pc := 1"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 1"]
		actions(e.end_algorithm) == []
	}

	def "translate a complicated while if"() {
		when:
		def m = translate(mm.algorithm {
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
		def e = m.events
		e.enter_while0_if0_then != null
		guards(e.enter_while0_if0_then) == ["pc = 0", "x : ODD", "x = 2"]
		actions(e.enter_while0_if0_then) == ["x := x + 1", "pc := 1"]

		e.enter_while0_if0_else_if1_then != null
		guards(e.enter_while0_if0_else_if1_then) == [
			"pc = 0",
			"x : ODD",
			"not(x = 2)",
			"x = 3"
		]
		actions(e.enter_while0_if0_else_if1_then) == ["x := x + 2", "pc := 1"]

		e.enter_while0_if0_else_if1_else_if2_then != null
		guards(e.enter_while0_if0_else_if1_else_if2_then) == [
			"pc = 0",
			"x : ODD",
			"not(x = 2)",
			"not(x = 3)",
			"x = 4"
		]
		actions(e.enter_while0_if0_else_if1_else_if2_then) == ["x := x + 3", "pc := 1"]

		e.enter_while0_if0_else_if1_else_if2_else != null
		guards(e.enter_while0_if0_else_if1_else_if2_else) == [
			"pc = 0",
			"x : ODD",
			"not(x = 2)",
			"not(x = 3)",
			"not(x = 4)"
		]
		actions(e.enter_while0_if0_else_if1_else_if2_else) == ["x := x - 5", "pc := 1"]

		e.if3_then != null
		guards(e.if3_then) == ["pc = 1", "y = 3"]
		actions(e.if3_then) == ["x := y + 2", "pc := 2"]

		e.if3_else != null
		guards(e.if3_else) == ["pc = 1", "not(y = 3)"]
		actions(e.if3_else) == ["x := y + 3", "pc := 2"]

		e.assign6 != null
		guards(e.assign6) == ["pc = 2"]
		actions(e.assign6) == ["x := y - 2", "pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x : ODD)"]
		actions(e.exit_while0) == ["z := x + y", "pc := 3"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 3"]
		actions(e.end_algorithm) == []
	}

	def "translate a loopity loop loop loop"() {
		when:
		def m = translate(mm.algorithm {
			While("x < 50") {
				If("y > x") {
					Then {
						While("x < y") { Assign("x := x + 1") }
					}
				}
			}
			While("z < 50") {
				If ("z < 0") { Then("z := 0 - z") }
				Assign("z := z + 1")
			}
		})

		then:
		def e = m.events
		e.enter_while0_if0_then != null
		guards(e.enter_while0_if0_then) == ["pc = 0", "x < 50", "y > x"]
		actions(e.enter_while0_if0_then) == ["pc := 1"]

		e.enter_while1 != null
		guards(e.enter_while1) == ["pc = 1", "x < y"]
		actions(e.enter_while1) == ["x := x + 1", "pc := 1"]

		e.exit_while1 != null
		guards(e.exit_while1) == ["pc = 1", "not(x < y)"]
		actions(e.exit_while1) == ["pc := 0"]

		e.enter_while0_if0_else != null
		guards(e.enter_while0_if0_else) == [
			"pc = 0",
			"x < 50",
			"not(y > x)"
		]
		actions(e.enter_while0_if0_else) == ["pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 50)"]
		actions(e.exit_while0) == ["pc := 2"]

		e.enter_while2_if1_then != null
		guards(e.enter_while2_if1_then) == ["pc = 2", "z < 50", "z < 0"]
		actions(e.enter_while2_if1_then) == ["z := 0 - z", "pc := 3"]

		e.assign2 != null
		guards(e.assign2) == ["pc = 3"]
		actions(e.assign2) == ["z := z + 1", "pc := 2"]

		e.enter_while2_if1_else != null
		guards(e.enter_while2_if1_else) == [
			"pc = 2",
			"z < 50",
			"not(z < 0)"
		]
		actions(e.enter_while2_if1_else) == ["pc := 3"]

		e.exit_while2 != null
		guards(e.exit_while2) == ["pc = 2", "not(z < 50)"]
		actions(e.exit_while2) == ["pc := 4"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 4"]
		actions(e.end_algorithm) == []
	}

	def "euclid algorithm"() {
		when:
		def m = translate(mm.algorithm {
			While("u /= 0") {
				If ("u < v") { Then("u,v := v,u") }
				Assert("u > v")
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		def e = m.events
		e.enter_while0_if0_then != null
		guards(e.enter_while0_if0_then) == ["pc = 0", "u /= 0", "u < v"]
		actions(e.enter_while0_if0_then) == [
			"u,v := v,u",
			"pc := 1"
		]

		e.enter_while0_if0_else != null
		guards(e.enter_while0_if0_else) == [
			"pc = 0",
			"u /= 0",
			"not(u < v)"
		]
		actions(e.enter_while0_if0_else) == ["pc := 1"]

		e.assign1 != null
		guards(e.assign1) == ["pc = 1"]
		actions(e.assign1) == ["u := u - v", "pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == [
			"pc = 0",
			"not(u /= 0)",
		]
		actions(e.exit_while0) == ["pc := 2"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 2"]
		actions(e.end_algorithm) == []
	}

	def "empty if then while"() {
		when:
		def m = translate(mm.algorithm {
			If("u < v") {}
			While("u /= 0") { Assign("u := u - 0") }
		})

		then:
		def e = m.events
		e.if0_then != null
		guards(e.if0_then) == ["pc = 0", "u < v"]
		actions(e.if0_then) == ["pc := 1"]

		e.if0_else != null
		guards(e.if0_else) == ["pc = 0", "not(u < v)"]
		actions(e.if0_else) == ["pc := 1"]

		e.enter_while0 != null
		guards(e.enter_while0) == ["pc = 1", "u /= 0"]
		actions(e.enter_while0) == ["u := u - 0", "pc := 1"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 1", "not(u /= 0)"]
		actions(e.exit_while0) == ["pc := 2"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 2"]
		actions(e.end_algorithm) == []
	}

	def "test assertion generation"() {
		when:
		def m = translate(mm.algorithm {
			Assert("1 = 1")
			While("x < 1") {
				Assert("2 = 2")
				If("x + y > 10") {
					Then {
						Assert("3 = 3")
						Assign("x := 2")
						Assert("4 = 4")
					}
				}
				Assert("5 = 5")
				Assign("x := z - 90")
				Assert("6 = 6")
			}
			Assert("7 = 7")
		})

		then:
		def i = m.invariants
		inv(i.assert0) == "pc = 0 => (1 = 1)"
		inv(i.assert1) == "pc = 0 & x < 1 => (2 = 2)"
		inv(i.assert2) == "pc = 0 & x < 1 & x + y > 10 => (3 = 3)"
		inv(i.assert3) == "pc = 1 => (4 = 4)"
		inv(i.assert4) == "pc = 2 => (5 = 5)"
		inv(i.assert5) == "pc = 3 => (6 = 6)"
		inv(i.assert6) == "pc = 4 => (7 = 7)"

		def e = m.events
		e.enter_while0_if0_then != null
		guards(e.enter_while0_if0_then) == [
			"pc = 0",
			"x < 1",
			"x + y > 10"
		]
		actions(e.enter_while0_if0_then) == ["x := 2", "pc := 1"]

		e.if0_then_end != null
		guards(e.if0_then_end) == ["pc = 1"]
		actions(e.if0_then_end) == ["pc := 2"]

		e.loop_to_while0 != null
		guards(e.loop_to_while0) == ["pc = 3"]
		actions(e.loop_to_while0) == ["pc := 0"]

		e.enter_while0_if0_else != null
		guards(e.enter_while0_if0_else) == [
			"pc = 0",
			"x < 1",
			"not(x + y > 10)"
		]
		actions(e.enter_while0_if0_else) == ["pc := 2"]

		e.assign1 != null
		guards(e.assign1) == ["pc = 2"]
		actions(e.assign1) == ["x := z - 90", "pc := 3"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 1)"]
		actions(e.exit_while0) == ["pc := 4"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 4"]
		actions(e.end_algorithm) == []
	}
}
