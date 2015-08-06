package de.prob.model.eventb

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event.EventType

class EventModifierTest extends Specification {

	def Event event
	def EventModifier modifier

	def setup() {
		event = new Event("myEvent", EventType.ORDINARY, false)
		modifier = new EventModifier(event)
	}

	def "it is possible to add a guard"() {
		when:
		def modifier = modifier.guard("x : NAT")

		then:
		modifier.getEvent().guards[0].getPredicate().toUnicode() == new EventB("x : NAT").toUnicode()
	}

	def "it is possible to remove a guard once added"() {
		when:
		def modifier = modifier.guard("x : NAT")
		def grd = modifier.getEvent().guards[0]
		modifier = modifier.removeGuard(grd)

		then:
		modifier.getEvent().guards.isEmpty()
	}

	def "it is possible to add an action"() {
		when:
		def modifier = modifier.action("x := 3")

		then:
		modifier.getEvent().actions[0].getCode().toUnicode() == new EventB("x := 3").toUnicode()
	}

	def "it is possible to remove an action once added"() {
		when:
		def modifier = modifier.action("x := 3")
		def action = modifier.getEvent().actions[0]
		modifier = modifier.removeAction(action)

		then:
		modifier.getEvent().actions.isEmpty()
	}

	def "it is possible to add a parameter"() {
		when:
		def modifier = modifier.parameter("x")

		then:
		modifier.getEvent().parameters[0].name == "x"
	}

	def "it is possible to remove a parameter block once added"() {
		when:
		def modifier = modifier.parameter("x")
		def p = modifier.getEvent().parameters[0]
		modifier = modifier.removeParameter(p)

		then:
		modifier.getEvent().parameters.isEmpty()
	}

	def "guard names are generated correctly"() {
		when:
		modifier = modifier.guard(grd4: "1 = 1")
		modifier = modifier.guard("2 = 2")
		modifier = modifier.guard(grd10: "3 = 3")
		modifier = modifier.guard("4 = 4")
		modifier = modifier.guard("5 = 5")

		then:
		modifier.getEvent().guards.collect { it.getName() } == ["grd4","grd5","grd10","grd11","grd12"]
	}

	def "action names are generated correctly"() {
		when:
		modifier = modifier.action(act4: "1 = 1")
		modifier = modifier.action("2 = 2")
		modifier = modifier.action(act10: "3 = 3")
		modifier = modifier.action("4 = 4")
		modifier = modifier.action("5 = 5")

		then:
		modifier.getEvent().actions.collect { it.getName() } == ["act4","act5","act10","act11","act12"]
	}
}
