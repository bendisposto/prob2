package de.prob.model.eventb.algorithm

import spock.lang.Specification
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.MachineModifier

class AlgorithmTranslation extends Specification {
	def MachineModifier mm
	def EventBMachine m

	def setup() {
		m = new EventBMachine("algorithm")
		mm = new MachineModifier(m)
	}

	def guards(Event evt) {
		evt.guards.collect { it.getPredicate().getCode() }
	}

	def actions(Event evt) {
		evt.actions.collect { it.getCode().getCode() }
	}

	def "translate an if without an else"() {
		when:
		mm.algorithm {
			If("x < 0") { Then("x := 0") }
		}

		then:
		def e = m.events
		e.evt0_if != null
		guards(e.evt0_if) == ["pc = 0", "x < 0"]
		actions(e.evt0_if) == ["pc := 1"]

		e.evt0_else != null
		guards(e.evt0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.evt0_else) == ["pc := 2"]

		e.evt1 != null
		guards(e.evt1) == ["pc = 1"]
		actions(e.evt1) == ["pc := 2", "x := 0"]

		e.evt2 != null
		guards(e.evt2) == ["pc = 2"]
		actions(e.evt2) == []
	}

	def "nextpc for if"() {
		when:
		def Block b = new Block().make({
			If("x < 0") {
				Then("x := 0")
				Else("x := 2")
			}
		})
		def a = new AlgorithmTranslator(mm)

		then:
		a.nextpc(0, b) == 4
		a.nextpc(0, b.statements[0]) == 4
		a.nextpc(1, b.statements[0].Then) == 2
		a.nextpc(3, b.statements[0].Else) == 4
	}

	def "translate an if with an else"() {
		when:
		mm.algorithm {
			If("x < 0") {
				Then("x := 0")
				Else("x := 2")
			}
		}

		then:
		def e = m.events
		e.evt0_if != null
		guards(e.evt0_if) == ["pc = 0", "x < 0"]
		actions(e.evt0_if) == ["pc := 1"]

		e.evt0_else != null
		guards(e.evt0_else) == ["pc = 0", "not(x < 0)"]
		actions(e.evt0_else) == ["pc := 3"]

		e.evt1 != null
		guards(e.evt1) == ["pc = 1"]
		actions(e.evt1) == ["pc := 2", "x := 0"]

		e.evt2_exit_if != null
		guards(e.evt2_exit_if) == ["pc = 2"]
		actions(e.evt2_exit_if) == ["pc := 4"]

		e.evt3 != null
		guards(e.evt3) == ["pc = 3"]
		actions(e.evt3) == ["pc := 4", "x := 2"]

		e.evt4 != null
		guards(e.evt4) == ["pc = 4"]
		actions(e.evt4) == []
	}

	def "translate a while loop"() {
		when:
		mm.algorithm {
			While("x < 0") { Assign("x := 0") }
		}

		then:
		def e = m.events
		e.evt0_enter_while != null
		guards(e.evt0_enter_while) == ["pc = 0", "x < 0"]
		actions(e.evt0_enter_while) == ["pc := 1"]

		e.evt0_exit_while != null
		guards(e.evt0_exit_while) == ["pc = 0", "not(x < 0)"]
		actions(e.evt0_exit_while) == ["pc := 3"]

		e.evt1 != null
		guards(e.evt1) == ["pc = 1"]
		actions(e.evt1) == ["pc := 2", "x := 0"]

		e.evt2_loop != null
		guards(e.evt2_loop) == ["pc = 2"]
		actions(e.evt2_loop) == ["pc := 0"]

		e.evt3 != null
		guards(e.evt3) == ["pc = 3"]
		actions(e.evt3) == []
	}
}
