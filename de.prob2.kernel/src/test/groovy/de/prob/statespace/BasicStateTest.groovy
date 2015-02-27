package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.model.representation.CSPModel
import de.prob.scripting.ClassicalBFactory


class BasicStateTest extends Specification {

	static StateSpace s
	static State root
	static State firstState
	static State secondState

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.load(path) as StateSpace
		root = s.getRoot()
		firstState = root.$initialise_machine()
		secondState = firstState.new("pp=PID1")
	}

	def "toString is id"() {
		expect:
		root.toString() == "root"
	}

	def "id is id"() {
		expect:
		root.getId() == "root"
		firstState.getId() == "0"
	}

	def "you can also get the numerical id"() {
		expect:
		root.numericalId() == -1
		firstState.numericalId() == 0
	}

	def "it is possible to get the state representation of a state"() {
		when:
		def rep = secondState.getStateRep()

		then:
		firstState.eval(rep).getValue() == "FALSE"
		secondState.eval(rep).getValue() == "TRUE"
	}

	def "if the model is not of type B, then it will not be generated"() {
		setup:
		def oldmodel = s.getModel()
		def model = Mock(CSPModel)
		s.model = model

		expect:
		secondState.getStateRep() == "unknown"

		cleanup:
		s.model = oldmodel
	}
}
