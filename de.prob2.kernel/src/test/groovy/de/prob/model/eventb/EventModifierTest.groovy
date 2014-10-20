package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.eventb.Event.EventType
import de.prob.model.representation.ModelElementList

class EventModifierTest extends Specification {

	def Event event
	def EventModifier modifier

	def setup() {
		event = new Event(null, "myEvent", EventType.ORDINARY)
		event.addRefines(new ModelElementList<Event>())
		event.addActions(new ModelElementList<EventBAction>())
		event.addGuards(new ModelElementList<EventBGuard>())
		event.addWitness(new ModelElementList<Witness>())
		event.addParameters(new ModelElementList<EventParameter>())
		modifier = new EventModifier(event)
	}

	def "it is possible to add a guard"() {
		when:
		def guard = modifier.addGuard("x : NAT")

		then:
		event.guards.contains(guard)
	}

	def "it is possible to remove a guard once added"() {
		when:
		def guard = modifier.addGuard("x : NAT")
		def removed = modifier.removeGuard(guard)

		then:
		removed == true
	}

	def "it is possible to remove a guard after performing a deep copy"() {
		when:
		def guard = modifier.addGuard("x : NAT")
		def event2 = ModelModifier.deepCopy(null, event)
		def contained = event2.guards.contains(guard)
		def mod2 = new EventModifier(event2)
		def removed = mod2.removeGuard(guard)

		then:
		contained == true && removed == true && !event2.guards.contains(guard)
	}

	def "it is possible to add an action"() {
		when:
		def action = modifier.addAction("x := 3")

		then:
		event.actions.contains(action)
	}

	def "it is possible to remove an action once added"() {
		when:
		def action = modifier.addAction("x := 3")
		def removed = modifier.removeAction(action)

		then:
		removed == true
	}

	def "it is possible to remove an action after performing a deep copy"() {
		when:
		def action = modifier.addAction("x := 3")
		def event2 = ModelModifier.deepCopy(null, event)
		def contained = event2.actions.contains(action)
		def mod2 = new EventModifier(event2)
		def removed = mod2.removeAction(action)

		then:
		contained == true && removed == true && !event2.actions.contains(action)
	}

	def "it is possible to add a parameter and its typing guard"() {
		when:
		def paramBlock = modifier.addParameter("x", "x : {1,2,3}")

		then:
		event.parameters.contains(paramBlock.parameter) && event.guards.contains(paramBlock.typingGuard)
	}

	def "it is possible to remove a parameter block once added"() {
		when:
		def paramBlock = modifier.addParameter("x", "x : {1,2,3}")
		def removed = modifier.removeParameter(paramBlock)

		then:
		removed == true
	}

	def "it is possible to remove a parameter after performing a deep copy"() {
		when:
		def paramBlock = modifier.addParameter("x", "x : {1,2,3}")
		def event2 = ModelModifier.deepCopy(null, event)
		def contained1 = event2.parameters.contains(paramBlock.parameter)
		def contained2 = event2.guards.contains(paramBlock.typingGuard)
		def mod2 = new EventModifier(event2)
		def removed = mod2.removeParameter(paramBlock)

		then:
		removed && contained1 && contained2 && !event2.parameters.contains(paramBlock.parameter) && !event2.guards.contains(paramBlock.typingGuard)
	}
}
