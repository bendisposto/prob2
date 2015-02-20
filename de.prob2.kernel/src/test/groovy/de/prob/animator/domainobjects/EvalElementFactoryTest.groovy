package de.prob.animator.domainobjects

import spock.lang.Specification

class EvalElementFactoryTest extends Specification {

	def EventB eventB
	def ClassicalB classicalB
	def String eventBserialized
	def String classicalBserialized
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
		factory.deserialize(eventBserialized).getCode() == eventB.getCode()
	}

	def "Deserialization works for ClassicalB"() {
		expect:
		factory.deserialize(classicalBserialized).getCode() == classicalB.getCode()
	}

	//TODO: create integration test to test serialization and deserialization of CSP
}
