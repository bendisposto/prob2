package de.prob.animator.domainobjects

import spock.lang.Specification

class EvalElementFactoryTest extends Specification {
	private EventB eventB
	private ClassicalB classicalB
	private String eventBSerialized
	private String classicalBSerialized
	private EvalElementFactory factory

	def setup() {
		eventB = new EventB("1+1")
		classicalB = new ClassicalB("1+1")
		eventBSerialized = eventB.serialized()
		classicalBSerialized = classicalB.serialized()
		factory = new EvalElementFactory()
	}

	def "Serialization works for EventB"() {
		expect:
		eventBSerialized == "#EventB:1+1"
	}

	def "Serialization works for ClassicalB"() {
		expect:
		classicalBSerialized == "#ClassicalB:1+1"
	}

	def "Deserialization works for EventB"() {
		expect:
		factory.deserialize(eventBSerialized).code == eventB.code
	}

	def "Deserialization works for ClassicalB"() {
		expect:
		factory.deserialize(classicalBSerialized).code == classicalB.code
	}

	//TODO: create integration test to test serialization and deserialization of CSP
}
