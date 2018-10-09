package de.prob.model.eventb.algorithm

import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBInvariant
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier

import spock.lang.Specification

class NaiveAlgorithmTranslation extends Specification {
	private MachineModifier mm

	def setup() {
		final m = new EventBMachine("algorithm")
		mm = new MachineModifier(m)
	}

	def translate(MachineModifier mm) {
		ModelModifier modelM = new ModelModifier().addMachine(mm.getMachine())
		String name = mm.getMachine().getName()
		modelM = new AlgorithmTranslator(modelM.getModel(), new AlgorithmGenerationOptions()).runTranslation(modelM, name)
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
		actions(e.if0_then) == ["pc := 1"]

		e.if0_else != null
		guards(e.if0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.if0_else) == ["pc := 2"]

		e.assign0 != null
		guards(e.assign0) == ["pc = 1"]
		actions(e.assign0) == ["x := 0", "pc := 2"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 2"]
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
		actions(e.if0_then) == ["pc := 1"]

		e.if0_else != null
		guards(e.if0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.if0_else) == ["pc := 2"]

		e.assign0 != null
		guards(e.assign0) == ["pc = 1"]
		actions(e.assign0) == ["x := 0", "pc := 3"]

		e.assign1 != null
		guards(e.assign1) == ["pc = 2"]
		actions(e.assign1) == ["x := 2", "pc := 3"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 3"]
		actions(e.end_algorithm) == []
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
		actions(e.enter_while0) == ["pc := 1"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 0)"]
		actions(e.exit_while0) == ["pc := 2"]

		e.assign0 != null
		guards(e.assign0) == ["pc = 1"]
		actions(e.assign0) == ["x := 0", "pc := 0"]

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
		inv(i.assert1) == "pc = 1 => (2 = 2)"
		inv(i.assert2) == "pc = 2 => (3 = 3)"
		inv(i.assert3) == "pc = 3 => (4 = 4)"
		inv(i.assert4) == "pc = 4 => (5 = 5)"
		inv(i.assert5) == "pc = 5 => (6 = 6)"
		inv(i.assert6) == "pc = 6 => (7 = 7)"

		def e = m.events
		e.enter_while0 != null
		guards(e.enter_while0) == ["pc = 0", "x < 1"]
		actions(e.enter_while0) == ["pc := 1"]

		e.if0_then != null
		guards(e.if0_then) == ["pc = 1", "x + y > 10"]
		actions(e.if0_then) == ["pc := 2"]

		e.assign0 != null
		guards(e.assign0) == ["pc = 2"]
		actions(e.assign0) == ["x := 2", "pc := 3"]

		e.if0_then_end != null
		guards(e.if0_then_end) == ["pc = 3"]
		actions(e.if0_then_end) == ["pc := 4"]

		e.if0_else != null
		guards(e.if0_else) == [
			"pc = 1",
			"not(x + y > 10)"
		]
		actions(e.if0_else) == ["pc := 4"]

		e.assign1 != null
		guards(e.assign1) == ["pc = 4"]
		actions(e.assign1) == ["x := z - 90", "pc := 5"]

		e.loop_to_while0 != null
		guards(e.loop_to_while0) == ["pc = 5"]
		actions(e.loop_to_while0) == ["pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 1)"]
		actions(e.exit_while0) == ["pc := 6"]

		e.end_algorithm != null
		guards(e.end_algorithm) == ["pc = 6"]
		actions(e.end_algorithm) == []
	}
}
