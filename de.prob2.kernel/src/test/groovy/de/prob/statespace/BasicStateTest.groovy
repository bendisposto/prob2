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

	def "equivalence in a state is based on id and state space"() {
		when:
		def sameroot = new State("root", s)
		def otherroot = new State("root", Mock(StateSpace))

		then:
		root == sameroot
		sameroot == root
		root != otherroot
		otherroot != root
	}

	def "a state is not equal to something else"() {
		expect:
		root != "I'm not a State!"
	}

	def "hashcode is based also on id and state space"() {
		when:
		def sameroot = new State("root", s)
		def otherroot = new State("root", Mock(StateSpace))

		then:
		root.hashCode() == sameroot.hashCode()
		root.hashCode() != otherroot.hashCode()
	}

	def "isInitialised contacts Prolog to check initialisation status (if it isn't explored)"() {
		setup:
		root.initialised = true
		root.explored = false

		when:
		def init = root.isInitialised()

		then:
		!init
		root.isExplored()
	}

	def "isInvariantOk contacts Prolog to check invariant status (if it isn't explored)"() {
		setup:
		firstState.invariantOk = false
		firstState.explored = false

		when:
		def invok = firstState.isInvariantOk()

		then:
		invok
		firstState.isExplored()
	}
}
