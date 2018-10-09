package de.prob.model.eventb

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.ElementComment

import org.eventb.core.ast.extension.IFormulaExtension

import spock.lang.Specification

class EventModifierTest extends Specification {
	private Event event
	private EventModifier modifier

	def setup() {
		event = new Event("myEvent", EventType.ORDINARY, false)
		modifier = new EventModifier(event)
	}

	def "constructor with event and default values"() {
		when:
		modifier = new EventModifier(event)

		then:
		modifier.getEvent() == event
		modifier.initialisation == false
		modifier.typeEnvironment == [] as Set
	}

	def "constructor with initialisation=true"() {
		when:
		modifier = new EventModifier(event, true)

		then:
		modifier.getEvent() == event
		modifier.initialisation == true
		modifier.typeEnvironment == [] as Set
	}

	def "construction w/ init and typenv"() {
		when:
		def typeenv = [Mock(IFormulaExtension)] as Set
		modifier = new EventModifier(event, true,typeenv)

		then:
		modifier.getEvent() == event
		modifier.initialisation == true
		modifier.typeEnvironment == typeenv
	}

	def "it is possible to set a refined event"() {
		when:
		def event = new Event("RefinedEvent", EventType.ORDINARY, false)
		modifier = modifier.refines(event, false)

		then:
		modifier.getEvent().getRefines() == [event]
	}

	def "refines cannot be null"() {
		when:
		modifier.refines(null, false)

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add a guard"() {
		when:
		def modifier = modifier.guard("x : NAT")

		then:
		modifier.getEvent().guards[0].getPredicate().toUnicode() == new EventB("x : NAT").toUnicode()
	}

	def "it is possible to add a guard from EventB"() {
		when:
		def grd = new EventB("x : NAT")
		def modifier = modifier.guard(grd)

		then:
		modifier.getEvent().guards[0].getPredicate() == grd
	}

	def "it is possible to add a labelled guard from EventB"() {
		when:
		def grd = new EventB("x : NAT")
		def modifier = modifier.guard("mygrd", grd)

		then:
		modifier.getEvent().guards.mygrd.getPredicate() == grd
	}

	def "it is possible to add a commented guard"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.guard("grd", "x : NAT", false, mycomment)

		then:
		modifier.getEvent().guards.grd.getComment() == mycomment
	}

	def "it is not possible to add a guard to initialisation"() {
		when:
		def em = new EventModifier(new Event("myEvent", EventType.ORDINARY, false), true)
		em.guard("grd", "x < 4")

		then:
		thrown IllegalArgumentException
	}

	def "it is not possible to add an EventB guard to initialisation"() {
		when:
		def em = new EventModifier(new Event("myEvent", EventType.ORDINARY, false), true)
		em.guard("grd", new EventB("x < 4"))

		then:
		thrown IllegalArgumentException
	}

	def "guard name cannot be null"() {
		when:
		modifier.guard(null, "x > 4")

		then:
		thrown IllegalArgumentException
	}

	def "EventB guard name cannot be null"() {
		when:
		modifier.guard(null, new EventB("x > 4"))

		then:
		thrown IllegalArgumentException
	}

	def "EventB guard must still be of type predicate"() {
		when:
		modifier.guard(new EventB("1+1"))

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "PREDICATE"
	}

	def "guard predicate cannot be null"() {
		when:
		modifier.guard("grd", null)

		then:
		thrown IllegalArgumentException
	}

	def "guard null comment => empty comment"() {
		when:
		modifier = modifier.guard("grd", "x < 4", false, null)

		then:
		modifier.getEvent().guards.grd.getComment() == ""
	}

	def "it is possible to add multiple guards (when)"() {
		when:
		modifier = modifier.when "x < 4", "y > 5", "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "it is possible to add multiple guards from map (when)"() {
		when:
		modifier = modifier.when grd: "x < 4", c: "y > 5", label: "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getName() } == ["grd", "c", "label"]
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "guards cannot be null (when)"() {
		when:
		modifier.when(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple guards cannot be null (when)"() {
		when:
		modifier.when("x > 4", null, "x > 5")

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add multiple guards (where)"() {
		when:
		modifier = modifier.where "x < 4", "y > 5", "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "it is possible to add multiple guards from map (where)"() {
		when:
		modifier = modifier.where grd: "x < 4", c: "y > 5", label: "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getName() } == ["grd", "c", "label"]
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "guards cannot be null (where)"() {
		when:
		modifier.where(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple guards cannot be null (where)"() {
		when:
		modifier.where("x > 4", null, "x > 5")

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add multiple guards"() {
		when:
		modifier = modifier.guards "x < 4", "y > 5", "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "it is possible to add multiple guards from map"() {
		when:
		modifier = modifier.guards grd: "x < 4", c: "y > 5", label: "z = 6"

		then:
		modifier.getEvent().guards.collect { it.getName() } == ["grd", "c", "label"]
		modifier.getEvent().guards.collect { it.getPredicate().getCode() } == ["x < 4", "y > 5", "z = 6"]
	}

	def "guards cannot be null"() {
		when:
		modifier.guards(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple guards cannot be null"() {
		when:
		modifier.guards("x > 4", null, "x > 5")

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add a theorem from map"() {
		when:
		modifier = modifier.theorem thm: "x < 4"

		then:
		modifier.getEvent().guards.thm.getPredicate().getCode() == "x < 4"
		modifier.getEvent().guards.thm.isTheorem()
	}

	def "it is possible to add a theorem"() {
		when:
		modifier = modifier.theorem "x < 4"

		then:
		modifier.getEvent().guards[0].getPredicate().getCode() == "x < 4"
		modifier.getEvent().guards[0].isTheorem()
	}

	def "theorem cannot be null"() {
		when:
		modifier.theorem(null)

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to remove a guard once added"() {
		when:
		def modifier = modifier.guard("x : NAT")
		def grd = modifier.getEvent().guards[0]
		modifier = modifier.removeGuard(grd)

		then:
		grd != null
		modifier.getEvent().guards.isEmpty()
	}


	def "it is possible to remove a guard via name once added"() {
		when:
		def modifier = modifier.guard(grd: "x : NAT")
		def grd = modifier.getEvent().guards.grd
		modifier = modifier.removeGuard("grd")

		then:
		grd != null
		modifier.getEvent().guards.isEmpty()
	}

	def "removing a nonexistant or null guard results does nothing"() {
		when:
		def idontexist = modifier.removeGuard("IDontExist")
		def iamnull = modifier.removeGuard(null)

		then:
		idontexist == modifier
		iamnull == modifier
	}

	def "it is possible to add multiple actions (then)"() {
		when:
		def modifier = modifier.then "x := 3", "y :: {1,2,3}", "z :| z'=z / 4"

		then:
		modifier.getEvent().actions.collect { it.getCode().getCode() } == [
			"x := 3",
			"y :: {1,2,3}",
			"z :| z'=z / 4"
		]
	}

	def "it is possible to add multiple actions from map (then)"() {
		when:
		def modifier = modifier.then a: "x := 3", b: "y :: {1,2,3}", c: "z :| z'=z / 4"

		then:
		modifier.getEvent().actions.collect { it.getName() } == ["a", "b", "c"]
		modifier.getEvent().actions.collect { it.getCode().getCode() } == [
			"x := 3",
			"y :: {1,2,3}",
			"z :| z'=z / 4"
		]
	}

	def "actions cannot be null (then)"() {
		when:
		modifier.then(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple actions cannot be null (then)"() {
		when:
		modifier.then("x := 1", null, "y := 2")

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add multiple actions"() {
		when:
		def modifier = modifier.actions "x := 3", "y :: {1,2,3}", "z :| z'=z / 4"

		then:
		modifier.getEvent().actions.collect { it.getCode().getCode() } == [
			"x := 3",
			"y :: {1,2,3}",
			"z :| z'=z / 4"
		]
	}

	def "it is possible to add multiple actions from map"() {
		when:
		def modifier = modifier.actions a: "x := 3", b: "y :: {1,2,3}", c: "z :| z'=z / 4"

		then:
		modifier.getEvent().actions.collect { it.getName() } == ["a", "b", "c"]
		modifier.getEvent().actions.collect { it.getCode().getCode() } == [
			"x := 3",
			"y :: {1,2,3}",
			"z :| z'=z / 4"
		]
	}

	def "actions cannot be null"() {
		when:
		modifier.actions(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple actions cannot be null"() {
		when:
		modifier.actions("x := 1", null, "y := 2")

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add an action"() {
		when:
		def modifier = modifier.action("x := 3")

		then:
		modifier.getEvent().actions[0].getCode().toUnicode() == new EventB("x := 3").toUnicode()
	}

	def "it is possible to add an EventB action"() {
		when:
		def act = new EventB("x := 3")
		def modifier = modifier.action(act)

		then:
		modifier.getEvent().actions[0].getCode() == act
	}

	def "it is possible to add a labelled EventB action"() {
		when:
		def act = new EventB("x := 3")
		def modifier = modifier.action("myact", act)

		then:
		modifier.getEvent().actions.myact.getCode() == act
	}

	def "EventB action must still be of type assignment"() {
		when:
		modifier.action(new EventB("1+1"))

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "ASSIGNMENT"
	}

	def "it is possible to add a commented action"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.action("act", "x := 1", mycomment)

		then:
		modifier.getEvent().actions.act.getComment() == mycomment
	}

	def "action name cannot be null"() {
		when:
		modifier.action(null, "x := 1")

		then:
		thrown IllegalArgumentException
	}

	def "action action cannot be null"() {
		when:
		modifier.action("act", null)

		then:
		thrown IllegalArgumentException
	}

	def "null comment for action results in empty comment"() {
		when:
		modifier = modifier.action("act", "x := 1", null)

		then:
		modifier.getEvent().actions.act.getComment() == ""
	}

	def "it is possible to remove an action once added"() {
		when:
		def modifier = modifier.action("x := 3")
		def action = modifier.getEvent().actions[0]
		modifier = modifier.removeAction(action)

		then:
		modifier.getEvent().actions.isEmpty()
	}

	def "it is possible to remove an action via name once added"() {
		when:
		def modifier = modifier.action act: "x := 3"
		def action = modifier.getEvent().actions.act
		modifier = modifier.removeAction("act")

		then:
		modifier.getEvent().actions.isEmpty()
	}

	def "removing non-existant or null action does nothing"() {
		when:
		def idontexist = modifier.removeAction("IDontExist")
		def iamnull	= modifier.removeAction(null)

		then:
		modifier == idontexist
		modifier == iamnull
	}

	def "it is possible to add multiple parameters (any)"() {
		when:
		modifier = modifier.any "x", "y", "z"

		then:
		modifier.getEvent().parameters.collect { it.getName() } == ["x", "y", "z"]
	}

	def "parameters cannot be null (any)"() {
		when:
		modifier.any null

		then:
		thrown IllegalArgumentException
	}

	def "multiple parameters cannot be null (any)"() {
		when:
		modifier.any "x", null, "y"

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add multiple parameters"() {
		when:
		modifier = modifier.parameters "x", "y", "z"

		then:
		modifier.getEvent().parameters.collect { it.getName() } == ["x", "y", "z"]
	}

	def "parameters cannot be null"() {
		when:
		modifier.parameters null

		then:
		thrown IllegalArgumentException
	}

	def "multiple parameters cannot be null"() {
		when:
		modifier.parameters "x", null, "y"

		then:
		thrown IllegalArgumentException
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

	def "parameter for parameter cannot be null"() {
		when:
		modifier.parameter null

		then:
		thrown IllegalArgumentException
	}

	def "it is not possible to add a parameter to initialisation"() {
		when:
		def em = new EventModifier(new Event("myEvent", EventType.ORDINARY, false), true)
		em.parameter("x")

		then:
		thrown IllegalArgumentException
	}

	def "parameter with null comment results in empty comment"() {
		when:
		modifier = modifier.parameter("x", null)

		then:
		modifier.getEvent().parameters.x.getComment() == ""
	}

	def "it is possible to remove a parameter block once added"() {
		when:
		def modifier = modifier.parameter("x")
		def p = modifier.getEvent().parameters[0]
		modifier = modifier.removeParameter(p)

		then:
		p != null
		modifier.getEvent().parameters.isEmpty()
	}

	def "it is possible to remove a parameter via name once added"() {
		when:
		def modifier = modifier.parameter("x")
		def p = modifier.getEvent().parameters.x
		modifier = modifier.removeParameter("x")

		then:
		p != null
		modifier.getEvent().parameters.isEmpty()
	}

	def "removing non-existant or null parameter does nothing"() {
		when:
		def idontexist = modifier.removeParameter("IDontExist")
		def iamnull	= modifier.removeParameter(null)

		then:
		modifier == idontexist
		modifier == iamnull
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
			"grd0",
			// grd0 doesn't exist yet, so it is added
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
			"act0",
			"act10",
			"act11",
			"act12"
		]
	}

	def "it is possible to add a witness (with)"() {
		when:
		modifier = modifier.with "x", "x > 5"

		then:
		modifier.getEvent().witnesses.collect { it.getName() } == ["x"]
		modifier.getEvent().witnesses.collect { it.getPredicate().getCode() } == ["x > 5"]
	}

	def "witness name cannot be null (with)"() {
		when:
		modifier.with(null,"x < 4")

		then:
		thrown(IllegalArgumentException)
	}

	def "witness predicate cannot be null (with)"() {
		when:
		modifier.with("x",null)

		then:
		thrown(IllegalArgumentException)
	}

	def "it is possible to add a witness from map"() {
		when:
		modifier = modifier.witness for: "x", with: "x > 5"

		then:
			modifier.getEvent().witnesses.collect { it.getName() } == ["x"]
		modifier.getEvent().witnesses.collect { it.getPredicate().getCode() } == ["x > 5"]
	}

	def "it is possible to add a witness"() {
		when:
		modifier = modifier.witness "x", "x > 5"

		then:
		modifier.getEvent().witnesses.collect { it.getName() } == ["x"]
		modifier.getEvent().witnesses.collect { it.getPredicate().getCode() } == ["x > 5"]
	}

	def "witness cannot be null"() {
		when:
		modifier.witness(null)

		then:
		thrown(IllegalArgumentException)
	}

	def "witness name cannot be null"() {
		when:
		modifier.witness(null,"x < 4")

		then:
		thrown(IllegalArgumentException)
	}

	def "witness predicate cannot be null"() {
		when:
		modifier.witness("x",null)

		then:
		thrown(IllegalArgumentException)
	}

	def "witness map cannot be empty"() {
		when:
		modifier.witness([:])

		then:
		thrown IllegalArgumentException
	}

	def "adding null comment for witness results in empty comment from map"() {
		when:
		def m1 = modifier.witness for: "x", with: "x > 5", null

		then:
			m1.getEvent().witnesses.x.getComment() == ""
	}

	def "adding null comment for witness results in empty comment"() {
		when:
		def m1 = modifier.witness "x", "x > 5", null

		then:
		m1.getEvent().witnesses.x.getComment() == ""
	}

	def "it is possible to add a commented witness"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.witness("x", "x : SET", mycomment)

		then:
		modifier.getEvent().witnesses.x.getComment() == mycomment
	}

	def "it is possible to remove a witness"() {
		when:
		modifier = modifier.witness("x", "x < 5")
		def wit = modifier.getEvent().witnesses.x
		modifier = modifier.removeWitness(wit)

		then:
		wit != null
		modifier.getEvent().witnesses.isEmpty()
	}

	def "it is possible to remove a witness via name"() {
		when:
		modifier = modifier.witness("x", "x < 5")
		def wit = modifier.getEvent().witnesses.x
		modifier = modifier.removeWitness("x")

		then:
		wit != null
		modifier.getEvent().witnesses.isEmpty()
	}

	def "it is not possible to remove a witness that doesn't exist"() {
		when:
		def wit = new Witness("x", new EventB("x < 5"), null)
		modifier = modifier.removeWitness(wit)
		def idontexist = modifier.removeWitness("y")
		def iamnull = modifier.removeWitness(null)

		then:
		wit != null
		modifier.getEvent().witnesses.isEmpty()
		idontexist == modifier
		iamnull == modifier
	}

	def "it is possible to set the type"() {
		when:
		def type = modifier.getEvent().getType()
		modifier = modifier.setType(EventType.ANTICIPATED)

		then:
		type == EventType.ORDINARY
		modifier.getEvent().getType() == EventType.ANTICIPATED
	}

	def "setType cannot be null"() {
		when:
		modifier.setType(null)

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add a comment to an event"() {
		when:
		String mycomment = "This is a comment!"
		modifier = modifier.addComment(mycomment)

		then:
		modifier.getEvent().getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "adding empty or null comment does nothing"() {
		when:
		def iamempty = modifier.addComment("")
		def iamnull = modifier.addComment(null)

		then:
		iamempty == modifier
		iamnull == modifier
	}

	def "make cannot be called with null"() {
		when:
		modifier.make null

		then:
		thrown IllegalArgumentException
	}

	def "make works correctly"() {
		when:
		modifier = modifier.make {
			any "x", "y"
			where "x < 10", "y > 20"
			with "z", "z = x + y"
			then "var1 := x + 1",
					"var2 := y - x"
		}
		def evt = modifier.getEvent()

		then:
		evt.parameters.collect { it.getName() } == ["x", "y"]
		evt.guards.collect { it.getPredicate().getCode() } == ["x < 10", "y > 20"]
		evt.witnesses.z.getPredicate().getCode() == "z = x + y"
		evt.actions.collect { it.getCode().getCode() } == [
			"var1 := x + 1",
			"var2 := y - x"
		]
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

	def "type error for witness when inputting invalid name"() {
		when:
		modifier.witness("x+1","1<2")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
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

	def "refined events cause the event counter to be modified"() {
		when:
		Event refined = new EventModifier(new Event("refined", EventType.ORDINARY, false)).make {
			when "x < y", "y < 6"
			then "x := x + 1", "y := y + 1", "z := z + 1"
		}.getEvent()
		def refacts = refined.actions.collect { it.getName() }
		def refgrds = refined.guards.collect { it.getName() }
		modifier = modifier.refines(refined, true).make {
			when "x < 4", "y < 6", "z + y < 10"
			then "x := x * 2", "y := 4 - x", "z := m - n"
		}
		def event = modifier.getEvent()

		then:
		refacts == ["act0", "act1", "act2"]
		refgrds == ["grd0", "grd1"]
		event.actions.collect { it.getName() } == ["act3", "act4", "act5"]
		event.guards.collect { it.getName() } == ["grd2", "grd3", "grd4"]
	}

	def "adding a refined event will rename existing actions"() {
		when:
		Event refined = new EventModifier(new Event("refined", EventType.ORDINARY, false)).make {
			when "x < y", "y < 6"
			then "x := x + 1", "y := y + 1", "z := z + 1"
		}.getEvent()
		def refacts = refined.actions.collect { it.getName() }
		def refgrds = refined.guards.collect { it.getName() }
		modifier = modifier.make {
			when "x < 4", "y < 6", "z + y < 10"
			then "x := x * 2", "y := 4 - x", "z := m - n"
		}
		def b4acts = modifier.getEvent().actions.collect { it.getName() }
		def b4grds = modifier.getEvent().guards.collect { it.getName() }
		def event = modifier.refines(refined, true).getEvent()

		then:
		refacts == ["act0", "act1", "act2"]
		refgrds == ["grd0", "grd1"]
		b4acts == ["act0", "act1", "act2"]
		b4grds == ["grd0", "grd1", "grd2"]
		event.actions.collect { it.getName() } == ["act3", "act4", "act5"]
		event.guards.collect { it.getName() } == ["grd2", "grd3", "grd4"]
	}

	def "renaming will only happen if necessary be changed"() {
		when:
		Event refined = new EventModifier(new Event("refined", EventType.ORDINARY, false)).make {
			when blah: "x < y", grd1: "y < 6"
			then foo: "x := x + 1", act0: "y := y + 1", act97: "z := z + 1"
		}.getEvent()
		def refacts = refined.actions.collect { it.getName() }
		def refgrds = refined.guards.collect { it.getName() }
		modifier = modifier.make {
			when "x < 4", "y < 6", "z + y < 10"
			then "x := x * 2", "y := 4 - x", "z := m - n"
		}
		def b4acts = modifier.getEvent().actions.collect { it.getName() }
		def b4grds = modifier.getEvent().guards.collect { it.getName() }
		def event = modifier.refines(refined, true).getEvent()

		then:
		refacts == ["foo", "act0", "act97"]
		refgrds == ["blah", "grd1"]
		b4acts == ["act0", "act1", "act2"]
		b4grds == ["grd0", "grd1", "grd2"]
		event.actions.collect { it.getName() } == ["act98", "act1", "act2"]
		event.guards.collect { it.getName() } == ["grd0", "grd2", "grd3"]
	}

	def "adding a an empty refined event will not affect naming"() {
		when:
		Event refined = new Event("refined", EventType.ORDINARY, false)
		def refacts = refined.actions.collect { it.getName() }
		def refgrds = refined.guards.collect { it.getName() }
		modifier = modifier.make {
			when "x < 4", "y < 6", "z + y < 10"
			then "x := x * 2", "y := 4 - x", "z := m - n"
		}
		def b4acts = modifier.getEvent().actions.collect { it.getName() }
		def b4grds = modifier.getEvent().guards.collect { it.getName() }
		def event = modifier.refines(refined, true).getEvent()

		then:
		refacts == []
		refgrds == []
		b4acts == ["act0", "act1", "act2"]
		b4grds == ["grd0", "grd1", "grd2"]
		event.actions.collect { it.getName() } == b4acts
		event.guards.collect { it.getName() } == b4grds
	}
}
