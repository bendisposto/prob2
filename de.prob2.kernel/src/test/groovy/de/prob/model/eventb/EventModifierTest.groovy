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

	def "it is possible to add a commented guard"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.guard("grd", "x : NAT", false, mycomment)

		then:
		modifier.getEvent().guards.grd.getComment() == mycomment
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

	def "it is possible to add a commented action"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.action("act", "x := 1", mycomment)

		then:
		modifier.getEvent().actions.act.getComment() == mycomment
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

	def "it is possible to add a commented parameter"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.parameter("x", mycomment)

		then:
		modifier.getEvent().parameters.x.getComment() == mycomment
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
		modifier.getEvent().guards.collect { it.getName() } == [
			"grd4",
			"grd5",
			"grd10",
			"grd11",
			"grd12"
		]
	}

	def "action names are generated correctly"() {
		when:
		modifier = modifier.action(act4: "x := 1")
		modifier = modifier.action("x := 2")
		modifier = modifier.action(act10: "x := 3")
		modifier = modifier.action("x := 4")
		modifier = modifier.action("x := 5")

		then:
		modifier.getEvent().actions.collect { it.getName() } == [
			"act4",
			"act5",
			"act10",
			"act11",
			"act12"
		]
	}

	def "it is possible to add a commented witness"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.witness("x", "x : SET", mycomment)

		then:
		modifier.getEvent().witnesses.x.getComment() == mycomment
	}

	def "parse error for witness when inputting invalid formula"() {
		when:
		modifier.witness("x","1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for guard when inputting invalid formula"() {
		when:
		modifier.guard("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for parameter when inputting invalid formula"() {
		when:
		modifier.parameter("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for action when inputting invalid formula"() {
		when:
		modifier.action("1+")

		then:
		thrown(FormulaParseException)
	}

	def "type error for witness when inputting expression"() {
		when:
		modifier.witness("x","1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "PREDICATE"
	}

	def "type error for guard when inputting expression"() {
		when:
		modifier.guard("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "PREDICATE"
	}

	def "type error for action when inputting expression"() {
		when:
		modifier.action("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "ASSIGNMENT"
	}

	def "type error for parameter when inputting expression"() {
		when:
		modifier.parameter("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for parameter when inputting predicate"() {
		when:
		modifier.parameter("1=1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}
}
