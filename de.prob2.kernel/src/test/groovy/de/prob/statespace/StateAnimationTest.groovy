package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.animator.domainobjects.ClassicalB
import de.prob.scripting.ClassicalBFactory


class StateAnimationTest extends Specification {

	static StateSpace s
	static State root
	static State firstState
	static State secondState

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
		root = s.getRoot()
		firstState = root.$initialise_machine()
		secondState = firstState.new("pp=PID1")
	}

	def "for invoking method, names can be escaped with a \$ if it is needed"() {
		expect:
		root.$$initialise_machine() == firstState
	}

	def "it is possible to treat events on the state as methods on the class"() {
		expect:
		root.$initialise_machine() == firstState
		firstState.new("pp=PID1").eval(new ClassicalB("waiting")).getValue() == "{PID1}"
		firstState.new("pp=PID1") == secondState
	}

	def "you can use the perform event to execute a transition"() {
		expect:
		root.perform("\$initialise_machine") == firstState
		firstState.perform("new","pp=PID1") == secondState
	}

	def "the perform event can take a list of predicates as an argument"() {
		expect:
		root.perform("\$initialise_machine",[]) == firstState
		firstState.perform("new",["pp=PID1"]) == secondState
	}

	def "performing an illegal event results in an exception"() {
		when:
		root.perform("blah",[])

		then:
		thrown(IllegalArgumentException)
	}

	def "you can find a transition that is outgoing from the current state vararg"() {
		expect:
		root.findTransition("\$initialise_machine").getName() == "\$initialise_machine"
		firstState.findTransition("new", "pp=PID1").getName() == "new"
	}

	def "you can find a transition that is outgoing from the current state with a list of predicates"() {
		expect:
		root.findTransition("\$initialise_machine").getName() == "\$initialise_machine"
		firstState.findTransition("new", "pp=PID1").getName() == "new"
	}

	def "you can't find an illegal transition"() {
		expect:
		root.findTransition("blah", []) == null
	}

	def "transition that is cached will be simply returned if no predicates are given"() {
		when:
		def t = Transition.generateArtificialTransition(s, "blah", "blah", "blah", "blah")
		root.transitions << t

		then:
		root.findTransition("blah",[]) == t
		root.transitions.remove(t)
	}

	def "you can find transitions with or without a predicate"() {
		expect:
		firstState.findTransitions("new", [], 3).size() == 3
		firstState.findTransitions("new", ["pp=PID1"], 1)[0].getParams() == ["PID1"]
	}

	def "can execute an event via the anyOperation method"() {
		when:
		State s2 = root.anyOperation()
		State s3 = root.anyOperation().anyOperation("new")
		State s4 = root.anyOperation().anyOperation(["new"])
		State s5 = root.anyOperation("blah") // will return original state

		then:
		s2 == firstState
		s3.eval("waiting").getValue() != "{}"
		s4.eval("waiting").getValue() != "{}"
		s5 == root
	}

	def "can execute an event via the anyEvent method"() {
		when:
		State s2 = root.anyEvent()
		State s3 = root.anyEvent().anyEvent("new")
		State s4 = root.anyEvent().anyEvent(["new"])
		State s5 = root.anyEvent("blah") // will return original state

		then:
		s2 == firstState
		s3.eval("waiting").getValue() != "{}"
		s4.eval("waiting").getValue() != "{}"
		s5 == root
	}
}
