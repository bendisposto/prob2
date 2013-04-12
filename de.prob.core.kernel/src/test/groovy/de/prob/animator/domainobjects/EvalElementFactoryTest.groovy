package de.prob.animator.domainobjects

import spock.lang.Specification

class EvalElementFactoryTest extends Specification {

	def eventB
	def classicalB
	def eventBserialized
	def classicalBserialized
	def factory

	def setup() {
		eventB = new EventB("1+1")
		classicalB = new ClassicalB("1+1")
		eventBserialized = eventB.serialized()
		classicalBserialized = classicalB.serialized()

		factory = new EvalElementFactory()
	}

	def "Serialization works for EventB"() {
		expect:
		eventBserialized == "#EventB:1+1"
	}

	def "Serialization works for ClassicalB"() {
		expect:
		classicalBserialized == "#ClassicalB:1+1"
	}

	def "Deserialization works for EventB"() {
		expect:
		factory.deserialize(eventBserialized) == eventB
	}

	def "Deserialization works for ClassicalB"() {
		expect:
		factory.deserialize(classicalBserialized) == classicalB
	}

	//TODO: create integration test to test serialization and deserialization of CSP
}
