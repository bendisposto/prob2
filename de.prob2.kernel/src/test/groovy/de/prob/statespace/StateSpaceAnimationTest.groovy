package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.IdentifierNotInitialised
import de.prob.scripting.ClassicalBFactory


class StateSpaceAnimationTest extends Specification {

	static StateSpace s
	static State root
	static State firstState

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.load(path) as StateSpace
		root = s.getRoot()
		firstState = root.$initialise_machine()
	}


	def "it is possible to get states based on a given predicate"() {
		when:
		firstState.new("pp=PID1").new("pp=PID2")
		def formula = "card(waiting) > 0" as ClassicalB
		def states = s.getStatesFromPredicate(formula)

		then:
		def evaluated =  states.collect { s.eval(it, [formula])[0] }
		evaluated.inject(true) { result, i -> result && (i instanceof IdentifierNotInitialised ||  i.getValue() == "TRUE")}
	}

	def "it is possible to calculate one transitions from a state and with a predicate"() {
		when:
		def transitions = s.transitionFromPredicate(firstState, "new", "pp=PID1", 1)

		then:
		transitions.size() == 1
		transitions[0].getName() == "new"
		transitions[0].getParams() == ["PID1"]
	}

	def "it is possible to calculate multiple transitions from a state with a predicate"() {
		when:
		def transitions = s.transitionFromPredicate(firstState, "new", "TRUE=TRUE", 3)

		then:
		transitions.size() == 3
		transitions.inject(true) { result, i -> result && (i.getName() == "new") }
		transitions.collect {it.getParams().first()} as Set == ["PID1", "PID2", "PID3"] as Set
	}

	def "trying to calculate zero transitions results an error"() {
		when:
		def transitions = s.transitionFromPredicate(firstState, "new", "TRUE=TRUE", 0)

		then:
		thrown(IllegalArgumentException)
	}

	def "trying to calculate transitions with an incorrect predicate results an error"() {
		when:
		def transitions = s.transitionFromPredicate(firstState, "new", "TRUE=true", 1)

		then:
		thrown(IllegalArgumentException)
	}

	def "it is possible to check if an operation is valid"() {
		expect:
		s.isValidOperation(firstState, "new", "pp=PID1")
	}

	def "it is possible to check if an operation is invalid"() {
		expect:
		!s.isValidOperation(firstState, "new", "TRUE=true")
	}

	def "it is possible to check if an operation is invalid because it has no solutions"() {
		expect:
		!s.isValidOperation(firstState, "new", "TRUE=FALSE")
	}

	def "it is possible to find a trace to a given state"() {
		when:
		State state = firstState.new("pp=PID1").new("pp=PID2").new("pp=PID3")
		Trace t = s.getTrace(state.getId())

		then:
		t != null
		t.getCurrentState() == state
	}

	def "it is possible to find a trace between two specified nodes"() {
		when:
		State state = firstState.new("pp=PID1").new("pp=PID2").new("pp=PID3")
		Trace t = s.getTrace(firstState.getId(), state.getId())

		then:
		t != null
		t.getTransitionList().first().getSource() == firstState
		t.getCurrentState() == state
	}

	def "it is possible to generate a trace from a list of transition ids"() {
		when:
		def transitions = []
		def tr = root.findTransition("\$initialise_machine")
		transitions << tr
		tr = tr.getDestination().findTransition("new","pp=PID1")
		transitions << tr
		tr = tr.getDestination().findTransition("new","pp=PID2")
		def Trace t = s.getTrace(transitions.collect { it.getId() })

		then:
		t != null
		t.getTransitionList().collect { it } == transitions
	}

	class MyTraceDescriptor implements ITraceDescription {

		@Override
		Trace getTrace(StateSpace s) throws RuntimeException {
			return new Trace(s).$initialise_machine().new("pp=PID1")
		}
	}

	def "it is possible to create ITraceDescription to generate traces"() {
		when:
		Trace t = s.getTrace(new MyTraceDescriptor())

		then:
		t != null
		t.getTransitionList().collect { it.getName() } == [
			"\$initialise_machine",
			"new"
		]
	}

	def "it is possible to generate a trace to a state in which a given predicate holds"() {
		when:
		def formula = "waiting = {PID1,PID3}" as ClassicalB
		Trace t = s.getTraceToState(formula)

		then:
		t != null
		t.evalCurrent(formula).getValue() == "TRUE"
	}
}
