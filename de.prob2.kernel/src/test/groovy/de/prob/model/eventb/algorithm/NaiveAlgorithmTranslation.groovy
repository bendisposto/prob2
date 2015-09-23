package de.prob.model.eventb.algorithm

import spock.lang.Specification
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.graph.NaiveGenerationAlgorithm

class NaiveAlgorithmTranslation extends Specification {
	def MachineModifier mm
	def EventBMachine m

	def setup() {
		m = new EventBMachine("algorithm")
		mm = new MachineModifier(m)
	}

	def translate(MachineModifier mm) {
		new AlgorithmTranslator(null, new NaiveGenerationAlgorithm()).translate(mm.getMachine())
	}

	def guards(Event evt) {
		evt.guards.collect { it.getPredicate().getCode() }
	}

	def actions(Event evt) {
		evt.actions.collect { it.getCode().getCode() }
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
}
