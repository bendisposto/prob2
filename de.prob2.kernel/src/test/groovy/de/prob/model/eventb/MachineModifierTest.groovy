package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.ElementComment

class MachineModifierTest extends Specification {
	def MachineModifier modifier

	def setup() {
		def machine = new EventBMachine("myMachine")

		modifier = new MachineModifier(machine)
	}

	def "it is possible to at an initialisation"() {
		when:
		modifier = modifier.initialisation { action "x := 1" }

		then:
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		!init.actions.isEmpty()
	}

	def "it is possible to add a variable"() {
		when:
		modifier = modifier.var_block("x", "x : NAT", "x := 0")

		then:
		modifier.getMachine().variables[0].getName() == "x"
		modifier.getMachine().invariants[0].getPredicate().getCode() == "x : NAT"
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		def actions = modifier.getMachine().events.INITIALISATION.getActions()
		!actions.isEmpty()
	}

	def "it is possible to add a commented variable"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.variable("x", mycomment)

		then:
		modifier.getMachine().variables.x.getComment() == mycomment
	}

	def "it is possible to remove a variable block once added"() {
		when:
		modifier = modifier.variable("x")
		def var = modifier.getMachine().variables[0]
		modifier = modifier.removeVariable(var)

		then:
		var != null
		modifier.getMachine().variables.isEmpty()
	}

	def "it is possible to add an invariant"() {
		when:
		modifier = modifier.invariant("x < 5")

		then:
		modifier.getMachine().invariants[0].getPredicate().getCode() == "x < 5"
	}

	def "it is possible to add a commented invariant"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.invariant("inv", "x : NAT", false, mycomment)

		then:
		modifier.getMachine().invariants.inv.getComment() == mycomment
	}

	def "it is possible to remove an invariant once added"() {
		when:
		modifier = modifier.invariant("x < 5")
		def inv = modifier.getMachine().invariants[0]
		modifier = modifier.removeInvariant(inv)

		then:
		inv != null
		modifier.getMachine().invariants.isEmpty()
	}

	def "it is possible to modify an existing event"() {
		when:
		modifier = modifier.var_block("x", "x : NAT", "x := 0")
		modifier = modifier.event(name: "INITIALISATION") { action "x := 1" }

		then:
		modifier.machine.events.INITIALISATION.actions.collect {
			it.getCode().getCode()
		} == ["x := 0", "x := 1"]
	}

	def "when an event doesn't exist, it can be added"() {
		when:
		def contained = modifier.getMachine().events.hasProperty("SomeEvent")
		modifier = modifier.event(name: "SomeEvent") {}

		then:
		!contained
		modifier.getMachine().events[0].getName() == "SomeEvent"
	}

	def "it is possible to remove an event after adding it"() {
		when:
		modifier = modifier.event(name: "SomeEvent") {
			parameter "x"
			guard "x : NAT"
			action "x := 1"
		}
		def event = modifier.getMachine().events[0]
		modifier = modifier.removeEvent(event)

		then:
		event != null
		modifier.getMachine().events.isEmpty()
	}

	def "it is possible to add a commented event"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.event(name: "myevent", comment: mycomment) {}

		then:
		modifier.getMachine().events.myevent.getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "it is possible to duplicate an event and add its duplicate to the machine"() {
		when:
		modifier = modifier.event(name: "event1") { action "x := 2" }
		modifier = modifier.duplicateEvent("event1", "event2")

		then:
		def event1 = modifier.getMachine().events.event1
		def event2 = modifier.getMachine().events.event2
		event1.getName() == "event1"
		event2.getName() == "event2"
		event1.getActions() == event2.getActions()
	}

	def "invariant names are generated correctly"() {
		when:
		modifier = modifier.invariant(inv4: "1 = 1")
		modifier = modifier.invariant("2 = 2")
		modifier = modifier.invariant(inv10: "3 = 3")
		modifier = modifier.invariant("4 = 4")
		modifier = modifier.invariant("5 = 5")

		then:
		modifier.getMachine().invariants.collect { it.getName() } == [
			"inv4",
			"inv5",
			"inv10",
			"inv11",
			"inv12"
		]
	}

	def "parse error for variable when inputting invalid formula"() {
		when:
		modifier.variable("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for invariant when inputting invalid formula"() {
		when:
		modifier.invariant("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for variant when inputting invalid formula"() {
		when:
		modifier.variant("1+")

		then:
		thrown(FormulaParseException)
	}

	def "type error for variable when inputting predicate"() {
		when:
		modifier.variable("1=1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for variable when inputting non identifier expression"() {
		when:
		modifier.variable("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for invariant when inputting expression"() {
		when:
		modifier.invariant("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "PREDICATE"
	}

	def "type error for variant when inputting predicate"() {
		when:
		modifier.variant("1=1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "EXPRESSION"
	}
}
