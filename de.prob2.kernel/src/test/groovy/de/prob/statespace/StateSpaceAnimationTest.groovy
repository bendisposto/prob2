package de.prob.statespace

import java.nio.file.Paths

import de.prob.Main
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.IdentifierNotInitialised
import de.prob.scripting.ClassicalBFactory

import spock.lang.Specification

class StateSpaceAnimationTest extends Specification {
	private static StateSpace s
	private static State root
	private static State firstState

	def setupSpec() {
		final path = Paths.get("groovyTests", "machines", "scheduler.mch").toString()
		ClassicalBFactory factory = Main.injector.getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
		root = s.root
		firstState = root.$initialise_machine()
	}

	def cleanupSpec() {
		s.kill()
	}

	def "it is possible to get states based on a given predicate"() {
		when:
		firstState.new("pp=PID1").new("pp=PID2")
		final formula = new ClassicalB("card(waiting) > 0", FormulaExpand.EXPAND)
		final states = s.getStatesFromPredicate(formula)

		then:
		final evaluated = states.collect {s.eval(it, [formula])[0]}
		evaluated.every {it instanceof IdentifierNotInitialised || it.value == "TRUE"}
	}

	def "it is possible to calculate one transitions from a state and with a predicate"() {
		when:
		final transitions = s.transitionFromPredicate(firstState, "new", "pp=PID1", 1)

		then:
		transitions.size() == 1
		transitions[0].name == "new"
		transitions[0].parameterValues == ["PID1"]
	}

	def "it is possible to calculate multiple transitions from a state with a predicate"() {
		when:
		final transitions = s.transitionFromPredicate(firstState, "new", "TRUE=TRUE", 3)

		then:
		transitions.size() == 3
		transitions.every {it.name == "new"}
		transitions.collect {it.parameterValues.first()} as Set == ["PID1", "PID2", "PID3"] as Set
	}

	def "trying to calculate zero transitions results an error"() {
		when:
		s.transitionFromPredicate(firstState, "new", "TRUE=TRUE", 0)

		then:
		thrown(IllegalArgumentException)
	}

	def "trying to calculate transitions with an incorrect predicate results an error"() {
		when:
		s.transitionFromPredicate(firstState, "new", "TRUE=true", 1)

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
		final state = firstState.new("pp=PID1").new("pp=PID2").new("pp=PID3")
		final t = s.getTrace(state.id)

		then:
		t != null
		t.currentState == state
	}

	def "it is possible to find a trace between two specified nodes"() {
		when:
		final state = firstState.new("pp=PID1").new("pp=PID2").new("pp=PID3")
		final t = s.getTrace(firstState.id, state.id)

		then:
		t != null
		t.transitionList.first().source == firstState
		t.currentState == state
	}

	def "it is possible to generate a trace from a list of transition ids"() {
		given:
		final transitions = []
		def tr = root.findTransition("\$initialise_machine")
		transitions << tr
		tr = tr.destination.findTransition("new","pp=PID1")
		transitions << tr
		tr = tr.destination.findTransition("new","pp=PID2")

		when:
		final t = s.getTrace(transitions.collect {it.id})

		then:
		t != null
		t.transitionList.collect() == transitions
	}

	def "it is possible to create ITraceDescription to generate traces"() {
		when:
		final t = s.getTrace(new ITraceDescription() {
			@Override
			Trace getTrace(StateSpace s) throws RuntimeException {
				return new Trace(s).$initialise_machine().new("pp=PID1")
			}
		})

		then:
		t != null
		t.transitionList.collect {it.name} == ["\$initialise_machine", "new"]
	}

	def "it is possible to generate a trace to a state in which a given predicate holds"() {
		given:
		final formula = new ClassicalB("waiting = {PID1,PID3}", FormulaExpand.EXPAND)

		when:
		final t = s.getTraceToState(formula)

		then:
		t != null
		t.evalCurrent(formula).value == "TRUE"
	}
}
