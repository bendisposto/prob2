package de.prob.statespace

import de.prob.Main
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel
import de.prob.scripting.ClassicalBFactory

import spock.lang.Specification

class BasicStateSpaceTest extends Specification {

	static StateSpace s

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
	}

	def cleanupSpec() {
		s.kill()
	}

	def "it is possible to cast a StateSpace to an AbstractModel"() {
		expect:
		s.getModel() == s as AbstractModel
	}

	def "it is possible to cast a StateSpace to its own Model class"() {
		expect:
		s.getModel() == s as ClassicalBModel
	}

	def "it is not possible to cast a StateSpace to its another Model class"() {
		when:
		s.getModel() == s as EventBModel

		then:
		thrown(ClassCastException)
	}

	def "it is possible to cast a StateSpace to a Trace object"() {
		expect:
		(s as Trace).getStateSpace() == s
	}

	def "it is not possible to cast a StateSpace to any other kind of class"() {
		when:
		s as String

		then:
		thrown(ClassCastException)
	}

	def "the id of a state space is the id of its animator"() {
		expect:
		s.getId() == s.animator.getId()
	}

	def "the to string method is implemented"() {
		expect:
		s.toString() != null
	}

	def "it is possible to print the operations for a given state"() {
		expect:
		s.printOps(s.getRoot()) != null
	}

	def "it is possible to print a state"() {
		expect:
		s.printState(s.getRoot()) != null
	}

	def "it states are not explored by default, not all transitions may be shown"() {
		when:
		Trace.exploreStateByDefault = false

		then:
		s.printOps(s.getRoot()).contains("Possibly not all transitions shown.")

		cleanup:
		Trace.exploreStateByDefault = true
	}
}
