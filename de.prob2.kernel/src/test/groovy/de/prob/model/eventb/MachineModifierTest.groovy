package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.ModelElementList

class MachineModifierTest extends Specification {
	def EventBMachine machine
	def MachineModifier modifier
	def model

	def setup() {
		model = new EventBModel(null)
		machine = new EventBMachine("myMachine")
		machine.addEvents(new ModelElementList<Event>())
		machine.addInvariants(new ModelElementList<EventBInvariant>(), new ModelElementList<EventBInvariant>())
		machine.addProofs(new ModelElementList<ProofObligation>())
		machine.addRefines(new ModelElementList<EventBMachine>())
		machine.addSees(new ModelElementList<Context>())
		machine.addVariables(new ModelElementList<EventBVariable>())
		machine.addVariant(new ModelElementList<Variant>())

		modifier = new MachineModifier(machine, [], [])
		modifier.addEvent("INITIALISATION")
	}

	def "it is possible to add a variable"() {
		when:
		VariableBlock block = modifier.addVariable("x", "x : NAT", "x := 0")

		then:
		machine.variables.contains(block.variable) &&
				machine.invariants.contains(block.typingInvariant) &&
				machine.events.INITIALISATION.actions.contains(block.initialisationAction)
	}

	def "it is possible to remove a variable block once added"() {
		when:
		VariableBlock block = modifier.addVariable("x", "x : NAT", "x := 0")
		def removed = modifier.removeVariableBlock(block)

		then:
		removed == true
	}

	def "it is possible to remove a variable after a deep copy"() {
		when:
		VariableBlock block = modifier.addVariable("x", "x : NAT", "x := 0")
		EventBMachine machine2 = ModelModifier.deepCopy(model, machine)
		def contained1 = machine2.variables.contains(block.variable)
		def contained2 = machine2.invariants.contains(block.typingInvariant)
		def contained3 = machine2.events.INITIALISATION.actions.contains(block.initialisationAction)
		MachineModifier mod2 = new MachineModifier(machine2)
		def removed = mod2.removeVariableBlock(block)

		then:
		removed && contained1 && contained2 && contained3 &&
				!machine2.variables.contains(block.variable) &&
				!machine2.invariants.contains(block.typingInvariant) &&
				!machine2.events.INITIALISATION.actions.contains(block.initialisationAction)
	}

	def "it is possible to add an invariant"() {
		when:
		def invariant = modifier.addInvariant("x < 5")

		then:
		machine.invariants.contains(invariant)
	}

	def "it is possible to remove an invariant once added"() {
		when:
		def invariant = modifier.addInvariant("x < 5")
		def removed = modifier.removeInvariant(invariant)

		then:
		removed
	}

	def "it is possible to remove an invariant after a deep copy"() {
		when:
		def invariant = modifier.addInvariant("x < 5")
		EventBMachine machine2 = ModelModifier.deepCopy(model, machine)
		def contained = machine2.invariants.contains(invariant)
		def mod2 = new MachineModifier(machine2)
		def removed = mod2.removeInvariant(invariant)

		then:
		removed && contained && !machine2.invariants.contains(invariant)
	}

	def "it is possible to get an existing event and modify it"() {
		when:
		VariableBlock block = modifier.addVariable("x", "x : NAT", "x := 0")
		EventModifier init = modifier.getEvent("INITIALISATION")
		def action = init.getEvent().actions[0]
		init.removeAction(action)
		def action2 = init.addAction("x := 1")

		then:
		machine.events.INITIALISATION.actions.contains(action2)
	}

	def "when an event doesn't exist, 'getting' it will add a new event"() {
		when:
		def contained = machine.events.hasProperty("SomeEvent")
		def event = modifier.getEvent("SomeEvent")

		then:
		!contained & machine.events.hasProperty("SomeEvent")
	}

	def "it is possible to remove an event after adding it"() {
		when:
		def newEventM = modifier.addEvent("SomeEvent")
		newEventM.addParameter("x", "x : NAT")
		newEventM.addAction("x := 1")
		def removed = modifier.removeEvent(newEventM.getEvent())

		then:
		removed
	}

	def "it is possible to remove an even after a deep copy"() {
		when:
		def newEventM = modifier.addEvent("SomeEvent")
		newEventM.addParameter("x", "x : NAT")
		newEventM.addAction("x := 1")
		EventBMachine machine2 = ModelModifier.deepCopy(model, machine)
		def contained = machine2.events.contains(newEventM.getEvent())
		def mod2 = new MachineModifier(machine2)
		def removed = mod2.removeEvent(newEventM.getEvent())

		then:
		contained & removed
	}

	def "it is possible to duplicate an event and add its duplicate to the machine"() {
		when:
		def newEventM = modifier.duplicateEvent(machine.events.INITIALISATION, "init2")

		then:
		machine.events.contains(newEventM.getEvent())
	}
}
