package de.prob.model.eventb.algorithm

import spock.lang.Specification
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBInvariant
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.representation.ModelElementList

class NaiveAlgorithmTranslation extends Specification {
	def MachineModifier mm
	def EventBMachine m

	def setup() {
		m = new EventBMachine("algorithm")
		mm = new MachineModifier(m)
	}

	def translate(MachineModifier mm) {
		new AlgorithmTranslator(new EventBModel(null), new AlgorithmGenerationOptions()).translate(mm.getMachine(), new ModelElementList<Procedure>())
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

		e.assign1 != null
		guards(e.assign1) == ["pc = 2"]
		actions(e.assign1) == []
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

		e.assign2 != null
		guards(e.assign2) == ["pc = 3"]
		actions(e.assign2) == []
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

		e.assign1 != null
		guards(e.assign1) == ["pc = 2"]
		actions(e.assign1) == []
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

		e.assign1 != null
		guards(e.assign1) == ["pc = 3"]
		actions(e.assign1) == ["pc := 4"]

		e.if0_else != null
		guards(e.if0_else) == [
			"pc = 1",
			"not(x + y > 10)"
		]
		actions(e.if0_else) == ["pc := 4"]

		e.assign2 != null
		guards(e.assign2) == ["pc = 4"]
		actions(e.assign2) == ["x := z - 90", "pc := 5"]

		e.assign3 != null
		guards(e.assign3) == ["pc = 5"]
		actions(e.assign3) == ["pc := 0"]

		e.exit_while0 != null
		guards(e.exit_while0) == ["pc = 0", "not(x < 1)"]
		actions(e.exit_while0) == ["pc := 6"]

		e.assign4 != null
		guards(e.assign4) == ["pc = 6"]
		actions(e.assign4) == []
	}
}
