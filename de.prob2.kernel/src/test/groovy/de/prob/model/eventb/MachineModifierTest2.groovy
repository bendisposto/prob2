package de.prob.model.eventb

import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.ModelElementList

import spock.lang.Specification

class MachineModifierTest2 extends Specification {
	private MachineModifier modifier

	def setup() {
		def machine = new EventBMachine("myMachine")

		modifier = new MachineModifier(machine, Collections.emptySet())
	}

	def po(String name) {
		new ProofObligation(null, name, false, "", [])
	}

	def "invariants cannot be null"() {
		when:
		modifier.invariants(null)

		then:
		thrown IllegalArgumentException
	}

	def "remove variable with null does nothing"() {
		when:
		def modifier1 = modifier.removeVariable(null)

		then:
		modifier1 == modifier
	}

	def "remove invariant with null does nothing"() {
		when:
		def modifier1 = modifier.removeInvariant(null)

		then:
		modifier1 == modifier
	}

	def "variant cannot be null"() {
		when:
		modifier.variant(null)

		then:
		thrown IllegalArgumentException
	}

	def "variant null comment -> empty comment"() {
		when:
		modifier = modifier.variant("x+1", null)

		then:
		modifier.getMachine().getVariant().getComment() == ""
	}

	def "removeVariant with null does nothing"() {
		when:
		def modifier1 = modifier.removeVariant(null)

		then:
		modifier1.getMachine().variant == modifier.getMachine().variant
	}

	def "initialisation cannot be null"() {
		when:
		modifier.initialisation(null)

		then:
		thrown IllegalArgumentException
	}

	def "refine cannot be null"() {
		when:
		modifier.refine(null)

		then:
		thrown IllegalArgumentException
	}

	def "refine closure cannot be null"() {
		when:
		modifier.refine(name: "x", null)

		then:
		thrown IllegalArgumentException
	}

	def "event map cannot be null"() {
		when:
		modifier.event(null)

		then:
		thrown IllegalArgumentException
	}

	def "event closure cannot be null"() {
		when:
		modifier.event(name: "m", null)

		then:
		thrown IllegalArgumentException
	}

	def "event name cannot be null"() {
		when:
		modifier.event(null, "ref", EventType.ORDINARY, false)

		then:
		thrown IllegalArgumentException
	}

	def "event type cannot be null"() {
		when:
		modifier.event("evt", null, null, false)

		then:
		thrown IllegalArgumentException
	}

	def "event closure (5 args) cannot be null"() {
		when:
		modifier.event("evt", null, EventType.ORDINARY, false, null, null)

		then:
		thrown IllegalArgumentException
	}

	def "duplicateEvent cannot be null"() {
		when:
		modifier.duplicateEvent(null, "x")

		then:
		thrown IllegalArgumentException
	}

	def "duplicateEvent newName cannot be null"() {
		when:
		modifier.event(name: "x").duplicateEvent("x", null)

		then:
		thrown IllegalArgumentException
	}

	def "removeEvent with null does nothing"() {
		when:
		def modifier1 = modifier.removeEvent(null)

		then:
		modifier1 == modifier
	}

	def "null or empty addComment does nothing"() {
		when:
		def modifier1 = modifier.addComment(null)
		def modifier2 = modifier.addComment("")

		then:
		modifier1 == modifier
		modifier2 == modifier
	}

	def "closure for make cannot be null"() {
		when:
		modifier.make(null)

		then:
		thrown IllegalArgumentException
	}

	def "removePOsForEvent cannot be null"() {
		when:
		def mypo = po("STH")
		def pos = new ModelElementList([mypo])//])
		def mch = modifier.getMachine().set(ProofObligation.class, pos)
		new MachineModifier(mch, [] as Set).removePOsForEvent(null)

		then:
		thrown IllegalArgumentException
	}
}
