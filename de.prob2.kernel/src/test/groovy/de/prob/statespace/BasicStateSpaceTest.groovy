package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel
import de.prob.scripting.ClassicalBFactory


class BasicStateSpaceTest extends Specification {

	static StateSpace s

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.load(path) as StateSpace
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
}
