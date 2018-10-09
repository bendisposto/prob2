package de.prob.model.eventb

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.representation.ElementComment
import de.prob.model.representation.ModelElementList

import org.eventb.core.ast.extension.IFormulaExtension

import spock.lang.Specification

class MachineModifierTest extends Specification {
	private MachineModifier modifier

	def setup() {
		def machine = new EventBMachine("myMachine")

		modifier = new MachineModifier(machine, Collections.emptySet())
	}

	def "constructor with machine => default value for typenv"() {
		when:
		def machine = new EventBMachine("m")
		modifier = new MachineModifier(machine)

		then:
		modifier.getMachine() == machine
		modifier.typeEnvironment == [] as Set
	}

	def "constructor with machine & type env"() {
		when:
		def machine = new EventBMachine("m")
		def typeEnv = [Mock(IFormulaExtension)] as Set
		modifier = new MachineModifier(machine, typeEnv)

		then:
		modifier.getMachine() == machine
		modifier.typeEnvironment == typeEnv
	}

	def "the input machine must not be null"() {
		when:
		new MachineModifier(null)

		then:
		thrown IllegalArgumentException
	}

	def "the type environment must not be null"() {
		when:
		modifier = new MachineModifier(new EventBMachine("myMachine"), null)

		then:
		modifier.typeEnvironment == [] as Set
	}

	def "it is possible to set the sees block"() {
		when:
		def sees = new ModelElementList([
			new Context("A"),
			new Context("B")
		])
		modifier = modifier.setSees(sees)

		then:
		modifier.getMachine().getSees() == sees
	}

	def "it is possible to set the refined machine"() {
		when:
		def mch = new EventBMachine("X")
		modifier = modifier.setRefines(mch)

		then:
		modifier.getMachine().getRefines() == [mch]
	}

	def "it is possible to add an initialisation"() {
		when:
		modifier = modifier.initialisation { action "x := 1" }

		then:
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		!init.actions.isEmpty()
	}

	def "it is possible to add a variable"() {
		when:
		modifier = modifier.var("x", "x : NAT", "x := 0")

		then:
		modifier.getMachine().variables[0].getName() == "x"
		modifier.getMachine().invariants[0].getPredicate().getCode() == "x : NAT"
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		def actions = modifier.getMachine().events.INITIALISATION.getActions()
		actions[0].getCode().getCode() == "x := 0"
	}

	def "it is possible to add a variable with inv and init"() {
		when:
		modifier = modifier.var name: "x", invariant: "x : NAT", init: "x := 0"

		then:
		modifier.getMachine().variables[0].getName() == "x"
		modifier.getMachine().invariants[0].getPredicate().getCode() == "x : NAT"
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		def actions = modifier.getMachine().events.INITIALISATION.getActions()
		actions[0].getCode().getCode() == "x := 0"
	}

	def "it is possible to add a variable with named inv and init"() {
		when:
		modifier = modifier.var "x", [inv: "x : NAT"], [act: "x := 0"]

		then:
		modifier.getMachine().variables[0].getName() == "x"
		modifier.getMachine().invariants.inv.getPredicate().getCode() == "x : NAT"
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		def actions = modifier.getMachine().events.INITIALISATION.getActions()
		actions.act.getCode().getCode() == "x := 0"
	}

	def "it is possible to add a variable with named inv and init from map"() {
		when:
		modifier = modifier.var name: "x",
		invariant: [inv: "x : NAT"],
		init: [act: "x := 0"]

		then:
		modifier.getMachine().variables[0].getName() == "x"
		modifier.getMachine().invariants.inv.getPredicate().getCode() == "x : NAT"
		def init = modifier.getMachine().events.INITIALISATION
		init != null
		def actions = modifier.getMachine().events.INITIALISATION.getActions()
		actions.act.getCode().getCode() == "x := 0"
	}

	def "it is possible to add a commented variable"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.variable("x", mycomment)

		then:
		modifier.getMachine().variables.x.getComment() == mycomment
	}

	def "add multiple variables"() {
		when:
		def modifier = modifier.variables("x","y","z")

		then:
		modifier.getMachine().variables.collect { it.getName() } == ["x", "y", "z"]
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

	def "it is possible to remove a variable via name once added"() {
		when:
		modifier = modifier.variable("x")
		def var = modifier.getMachine().variables[0]
		modifier = modifier.removeVariable("x")

		then:
		var.name == "x"
		modifier.getMachine().variables.isEmpty()
	}

	def "if there is no variable to remove, nothing happens"() {
		when:
		def modifier1 = modifier.variable("x")
		def modifier2 = modifier1.removeVariable("y")

		then:
		modifier1 == modifier2
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

	def "it is possible to add multiple named invariants"() {
		when:
		modifier = modifier.invariants inv1: "x : NAT",
		blah: "x = 4",
		moo: "r : NAT"
		def invariants = modifier.getMachine().invariants

		then:
		invariants.inv1.getPredicate().getCode() == "x : NAT"
		invariants.blah.getPredicate().getCode() == "x = 4"
		invariants.moo.getPredicate().getCode() == "r : NAT"
		!invariants.inv1.isTheorem()
		!invariants.blah.isTheorem()
		!invariants.moo.isTheorem()
	}

	def "it is possible to add multiple invariants"() {
		when:
		modifier = modifier.invariants "x : NAT",
				"x = 4",
				"r : NAT"
		def invariants = modifier.getMachine().invariants

		then:
		invariants.collect { it.getName() } == ["inv0", "inv1", "inv2"]
		invariants[0].getPredicate().getCode() == "x : NAT"
		invariants[1].getPredicate().getCode() == "x = 4"
		invariants[2].getPredicate().getCode() == "r : NAT"
		!invariants[0].isTheorem()
		!invariants[1].isTheorem()
		!invariants[1].isTheorem()
	}

	def po(String name) {
		new ProofObligation(null, name, false, "", [])
	}

	def "adding an invariant deletes all INV proof obligations from model"() {
		when:
		def notaninv = po("evt/WD")
		def pos = new ModelElementList([
			notaninv,
			po("evt/inv/INV"),
			po("evt/inv2/INV"),
			po("evt/inv3/INV")
		])
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		modifier = modifier.invariant("x < 4")

		then:
		modifier.getMachine().getProofs() == [notaninv]
	}

	def "it is possible to add a theorem"() {
		when:
		modifier = modifier.theorem("1 = 1")
		def thm = modifier.getMachine().invariants[0]

		then:
		thm.getName() == "thm0"
		thm.isTheorem()
		thm.getPredicate().getCode() == "1 = 1"
	}

	def "trying to add a theorem with null (1)"() {
		when:
		modifier = modifier.theorem(null)

		then:
		thrown IllegalArgumentException
	}


	def "trying to add a theorem with null (2)"() {
		when:
		modifier = modifier.theorem(null, "x < 4")

		then:
		thrown IllegalArgumentException
	}

	def "trying to add a theorem with null (3)"() {
		when:
		modifier = modifier.theorem("inv",null)

		then:
		thrown IllegalArgumentException
	}

	def "trying to add an invariant with null (1)"() {
		when:
		modifier = modifier.invariant(null)

		then:
		thrown IllegalArgumentException
	}


	def "trying to add an invariant with null (2)"() {
		when:
		modifier = modifier.invariant(null, "x < 4")

		then:
		thrown IllegalArgumentException
	}

	def "trying to add an invariant with null (2.5)"() {
		when:
		modifier = modifier.invariant(null, "x < 4", false)

		then:
		thrown IllegalArgumentException
	}

	def "trying to add an invariant with null (3)"() {
		when:
		modifier = modifier.invariant("inv",null)

		then:
		thrown IllegalArgumentException
	}

	def "trying to add an invariant with null (3.5)"() {
		when:
		modifier = modifier.invariant("inv", null, false)

		then:
		thrown IllegalArgumentException
	}

	def "trying to add an invariant with a null comment results in empty comment"() {
		when:
		modifier = modifier.invariant("inv", "x < 4", false, null)

		then:
		modifier.getMachine().invariants.inv.getComment() == ""
	}


	def "it is possible to add a theorem with a name"() {
		when:
		modifier = modifier.theorem("thm", "1 = 1")
		def thm = modifier.getMachine().invariants[0]

		then:
		thm.getName() == "thm"
		thm.isTheorem()
		thm.getPredicate().getCode() == "1 = 1"
	}

	def "it is possible to add a theorem from map definition"() {
		when:
		modifier = modifier.theorem thm: "1 = 1"
		def thm = modifier.getMachine().invariants[0]

		then:
		thm.getName() == "thm"
		thm.isTheorem()
		thm.getPredicate().getCode() == "1 = 1"
	}

	def "it is possible to add multiple named theorems"() {
		when:
		modifier = modifier.theorems inv1: "x : NAT",
		blah: "x = 4",
		moo: "r : NAT"
		def invariants = modifier.getMachine().invariants

		then:
		invariants.inv1.getPredicate().getCode() == "x : NAT"
		invariants.blah.getPredicate().getCode() == "x = 4"
		invariants.moo.getPredicate().getCode() == "r : NAT"
		invariants.inv1.isTheorem()
		invariants.blah.isTheorem()
		invariants.moo.isTheorem()
	}

	def "it is possible to add multiple theorems"() {
		when:
		modifier = modifier.theorems "x : NAT",
				"x = 4",
				"r : NAT"
		def invariants = modifier.getMachine().invariants

		then:
		invariants.collect { it.getName() } == ["thm0", "thm1", "thm2"]
		invariants[0].getPredicate().getCode() == "x : NAT"
		invariants[1].getPredicate().getCode() == "x = 4"
		invariants[2].getPredicate().getCode() == "r : NAT"
		invariants[0].isTheorem()
		invariants[1].isTheorem()
		invariants[1].isTheorem()
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

	def "it is possible to remove an invariant by name once added"() {
		when:
		modifier = modifier.invariant("myinv", "x < 5")
		def inv = modifier.getMachine().invariants.myinv
		modifier = modifier.removeInvariant("myinv")

		then:
		inv != null
		modifier.getMachine().invariants.isEmpty()
	}

	def "removing an invariant by name that doesn't exist will do nothing"() {
		when:
		def modifier1 = modifier
		def modifier2 = modifier1.removeInvariant("myinv")

		then:
		modifier1 == modifier2
	}

	def "removing an invariant removes all pos except VWD pos"() {
		when:
		def vwd = po("VWD")
		def pos = new ModelElementList([
			vwd,
			po("evt/inv1/INV"),
			po("inv2/THM"),
			po("inv3/WD")
		])
		modifier = modifier.invariant("inv1", "x < 4")
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		modifier = modifier.removeInvariant("inv1")

		then:
		modifier.getMachine().getProofs() == [vwd]
	}

	def "it is possible to modify an existing event"() {
		when:
		modifier = modifier.var("x", "x : NAT", "x := 0")
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

	def "it is not possible to duplicate an event if the duplicate is not there"() {
		when:
		modifier = modifier.duplicateEvent("event1", "event2")

		then:
		thrown IllegalArgumentException
	}


	def "it is possible to add an extended initialisation"() {
		when:
		modifier = modifier.initialisation(extended: true)

		then:
		modifier.getMachine().events
		modifier.getMachine().events.INITIALISATION.isExtended()
	}

	def "it if extended is not set for initialisation, nothing happens"() {
		when:
		def modifier1 = modifier.initialisation(blah: true)

		then:
		modifier1 == modifier
	}

	def "initialisation seeks out correct initialisation if it is there"() {
		when:
		def refined = new MachineModifier(new EventBMachine("refined"), [] as Set).make { var "x", "x : NAT", "x := 0" }.getMachine()
		def refinedinit = refined.events.INITIALISATION
		modifier = modifier.setRefines(refined)
		modifier = modifier.variable("y")
		modifier = modifier.invariant("y : INT")
		modifier = modifier.initialisation { action "y := -1" }
		def machine = modifier.getMachine()

		then:
		machine.events.INITIALISATION.getRefines() == [refinedinit]
	}

	def "when refining events, the correct refined event is selected"() {
		when:
		def refined = new MachineModifier(new EventBMachine("refined"), [] as Set).make {
			var "x", "x : NAT", "x := 0"
			event(name: "inc") { then "x := x + 1" }
		}.getMachine()
		def inc = refined.events.inc
		modifier = modifier.setRefines(refined)
		modifier = modifier.var("y", "y : INT", "y := -1")
		modifier = modifier.refine(name: "inc", extended: "true")
		def machine = modifier.getMachine()

		then:
		machine.events.inc.getRefines() == [inc]
	}

	def "when refining events with closure, the correct refined event is selected"() {
		when:
		def refined = new MachineModifier(new EventBMachine("refined"), [] as Set).make {
			var "x", "x : NAT", "x := 0"
			event(name: "inc") { then "x := x + 1" }
		}.getMachine()
		def inc = refined.events.inc
		modifier = modifier.setRefines(refined)
		modifier = modifier.var("y", "y : INT", "y := -1")
		modifier = modifier.refine(name: "inc", extended: "true") { then "y := y + 1" }
		def machine = modifier.getMachine()

		then:
		machine.events.inc.getRefines() == [inc]
	}

	def "when refining an event, it must exist in the refinement"() {
		when:
		def refined = new MachineModifier(new EventBMachine("refined"), [] as Set).make {
			var "x", "x : NAT", "x := 0"
			event(name: "inc") { then "x := x + 1" }
		}.getMachine()
		def inc = refined.events.inc
		modifier = modifier.setRefines(refined)
		modifier = modifier.var("y", "y : INT", "y := -1")
		modifier.refine(name: "inc2", extended: "true") { then "y := y + 1" }

		then:
		thrown IllegalArgumentException
	}

	def "when refining an event, there must be a refinement"() {
		when:
		modifier.refine(name: "inc2", extended: "true") { then "y := y + 1" }

		then:
		thrown IllegalArgumentException
	}

	def "event with map parameter requires name entry"() {
		when:
		modifier.event([:], {})

		then:
		thrown(IllegalArgumentException)
	}

	def "event not specifying other than name will have default values"() {
		when:
		modifier = modifier.event(name: "myevt") {}
		Event evt = modifier.getMachine().events.myevt

		then:
		evt.getChildrenOfType(Event.class).isEmpty()
		!evt.isExtended()
		evt.getType() == EventType.ORDINARY
		evt.getChildrenOfType(ElementComment.class).isEmpty()
	}

	def "add event from map description"() {
		when:
		modifier = modifier.event(name: "myevt")
		Event evt = modifier.getMachine().events.myevt

		then:
		evt.getChildrenOfType(Event.class).isEmpty()
		!evt.isExtended()
		evt.getType() == EventType.ORDINARY
		evt.getChildrenOfType(ElementComment.class).isEmpty()
	}

	def "it is possible to add an event"() {
		when:
		modifier = modifier.event("myevt", null, EventType.ORDINARY, false, null) { then "x := 1" }
		Event evt = modifier.getMachine().events.myevt

		then:
		evt.getChildrenOfType(Event.class).isEmpty()
		!evt.isExtended()
		evt.getType() == EventType.ORDINARY
		evt.getChildrenOfType(ElementComment.class).isEmpty()
		evt.getActions()[0].getCode().getCode() == "x := 1"
	}

	def "it is possible to add an event (2)"() {
		when:
		modifier = modifier.event("myevt", null, EventType.ORDINARY, false)
		Event evt = modifier.getMachine().events.myevt

		then:
		evt.getChildrenOfType(Event.class).isEmpty()
		!evt.isExtended()
		evt.getType() == EventType.ORDINARY
		evt.getChildrenOfType(ElementComment.class).isEmpty()
	}

	def "it is possible to add an event (3)"() {
		when:
		String mycomment = "This is a comment!"
		modifier = modifier.event("myevt", null, EventType.ORDINARY, false, mycomment)
		Event evt = modifier.getMachine().events.myevt

		then:
		evt.getChildrenOfType(Event.class).isEmpty()
		!evt.isExtended()
		evt.getType() == EventType.ORDINARY
		evt.getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "adding event removes correct POs"() {
		when:
		def evt1 = po("evt1/inv/INV")
		def evt2 = po("act/inv/INV")
		def pos = new ModelElementList<ProofObligation>([
			po("evt/inv/INV"),
			po("evt/grd/WD"),
			po("evt/grd/THM"),
			evt1,
			evt2
		])
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		modifier = modifier.event(name: "evt")

		then:
		modifier.getMachine().getProofs() == [evt1, evt2]
	}

	def "duplicating event removes correct POs"() {
		when:
		def evt1 = po("evt1/inv/INV")
		def evt2 = po("act/inv/INV")
		def pos = new ModelElementList<ProofObligation>([
			po("evt/inv/INV"),
			po("evt/grd/WD"),
			po("evt/grd/THM"),
			evt1,
			evt2
		])
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		modifier = modifier.event(name: "evt2")
		modifier = modifier.duplicateEvent("evt2", "evt")

		then:
		modifier.getMachine().getProofs() == [evt1, evt2]
	}

	def "it is possible to remove events via name"() {
		when:
		modifier = modifier.event(name: "blah")
		def evts = modifier.getMachine().events
		modifier = modifier.removeEvent("blah")

		then:
		modifier.getMachine().events.isEmpty()
		evts.size() == 1
	}

	def "if removing events via name, if it doesn't exist nothing happens"() {
		when:
		def modifier1 = modifier.removeEvent("blah")

		then:
		modifier1 == modifier
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
			"inv0",
			"inv10",
			"inv11",
			"inv12"
		]
	}

	def "it is possible to add a variant"() {
		when:
		Variant v = new Variant(new EventB("x"),"")
		modifier = modifier.variant(v)

		then:
		modifier.getMachine().getVariant() == v
	}

	def "it is possible to remove a variant once added"() {
		when:
		Variant v = new Variant(new EventB("x"),"")
		modifier = modifier.variant(v)
		Variant v2 = modifier.getMachine().getVariant()
		modifier = modifier.removeVariant(v)

		then:
		v2 != null
		modifier.getMachine().getVariant() == null
	}

	def "adding a variant results in relevant POs being removed"() {
		when:
		def inv = po("evt/inv/INV")
		def thm = po("inv2/THM")
		def wd = po("inv2/WD")
		def pos = new ModelElementList([
			inv,
			thm,
			wd,
			po("VWD"),
			po("FIN"),
			po("evt/VAR"),
			po("evt/NAT")
		])
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		Variant v = new Variant(new EventB("x"),"")
		modifier = modifier.variant(v)

		then:
		modifier.getMachine().getProofs() == [inv, thm, wd]
	}

	def "it is possible to add a variant via expression"() {
		when:
		modifier = modifier.variant("x + 1")

		then:
		modifier.getMachine().getVariant().getExpression().getCode() == "x + 1"
	}

	def "it is possible to add a variant with a comment"() {
		when:
		def mycomment = "This is a comment!"
		modifier = modifier.variant("x + 1", mycomment)

		then:
		modifier.getMachine().getVariant().getComment() == mycomment
	}

	def "removing a variant results in relevant POs being removed"() {
		when:
		def inv = po("evt/inv/INV")
		def thm = po("inv2/THM")
		def wd = po("inv2/WD")
		def pos = new ModelElementList([
			inv,
			thm,
			wd,
			po("VWD"),
			po("FIN"),
			po("evt/VAR"),
			po("evt/NAT")
		])
		Variant v = new Variant(new EventB("x"),"")
		modifier = modifier.variant(v)
		modifier = new MachineModifier(modifier.getMachine().set(ProofObligation.class, pos), [] as Set)
		modifier = modifier.removeVariant(v)

		then:
		modifier.getMachine().getProofs() == [inv, thm, wd]
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

	def "it is possible to add a comment to a machine"() {
		when:
		def comment1 = "My first comment!"
		def comment2 = "My second comment!"
		modifier = modifier.addComment(comment1)
		modifier = modifier.addComment(comment2)

		then:
		modifier.getMachine().getChildrenOfType(ElementComment.class).collect { it.getComment() } == [comment1, comment2]
	}

	def "it is possible to add an algorithm"() {
		when:
		modifier = modifier.algorithm {
			While("x < 10") { Assign("x := x + 1") }
		}

		then:
		!modifier.getMachine().getChildrenOfType(Block.class).isEmpty()
	}

	def "it is possible to add an algorithm block"() {
		when:
		def block = new Block().If("x < 10") { Then("x := x +1") }
		modifier = modifier.algorithm(block)

		then:
		modifier.getMachine().getChildrenOfType(Block.class) == [block]
	}

	def "algorithm definition cannot be null"() {
		when:
		modifier.algorithm(null)

		then:
		thrown IllegalArgumentException
	}

	def "set sees cannot be null"() {
		when:
		modifier = modifier.setSees(null)

		then:
		thrown IllegalArgumentException
	}

	def "refines cannot be null"() {
		when:
		modifier = modifier.setRefines(null)

		then:
		thrown IllegalArgumentException
	}

	def "empty variables does nothing"() {
		when:
		def modifier1 = modifier.variables()

		then:
		modifier1 == modifier
	}

	def "variables cannot be null"() {
		when:
		modifier.variables(null)

		then:
		thrown IllegalArgumentException
	}

	def "variable cannot be null"() {
		when:
		modifier = modifier.variable(null)

		then:
		thrown IllegalArgumentException
	}

	def "variable null comment results in empty comment"() {
		when:
		modifier = modifier.variable("x", null)

		then:
		modifier.getMachine().variables.x.getComment() == ""
	}

	def "var cannot be null"() {
		when:
		modifier = modifier.var(null)

		then:
		thrown IllegalArgumentException
	}

	def "var cannot be null (1)"() {
		when:
		modifier = modifier.var(null, null, null)

		then:
		thrown IllegalArgumentException
	}

	def "var cannot be null (2)"() {
		when:
		modifier = modifier.var("x", null, null)

		then:
		thrown IllegalArgumentException
	}

	def "var cannot be null (3)"() {
		when:
		modifier = modifier.var("x", "x < 4", null)

		then:
		thrown IllegalArgumentException
	}

	def "empty invariants does nothing"() {
		when:
		def modifier1 = modifier.invariants()
		def modifier2 = modifier.invariants([:])

		then:
		modifier1 == modifier
		modifier2 == modifier
	}
}
