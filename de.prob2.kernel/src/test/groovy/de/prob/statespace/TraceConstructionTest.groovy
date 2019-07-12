package de.prob.statespace

import java.nio.file.Paths

import de.prob.Main
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel
import de.prob.scripting.ClassicalBFactory

import spock.lang.Specification

class TraceConstructionTest extends Specification {
	private static StateSpace s

	def setupSpec() {
		final path = Paths.get("groovyTests", "machines", "scheduler.mch").toString()
		final factory = Main.injector.getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
	}

	def cleanupSpec() {
		s.kill()
	}

	def "can create Trace from StateSpace"() {
		expect:
		// The != null check is always true; this simply tests that no exception is thrown.
		new Trace(s) != null
	}

	def "cannot create Trace with null parameter"() {
		when:
		new Trace(null)

		then:
		thrown(RuntimeException)
	}

	def "casting trace with AbstractModel works"() {
		when:
		final t = new Trace(s)

		then:
		t.model == t as AbstractModel
	}

	def "casting trace with ClassicalBModel works if it is a classical b model"() {
		when:
		final t = new Trace(s)

		then:
		t.model == t as ClassicalBModel
	}

	def "casting trace with other model type (i.e. EventB) results in error"() {
		when:
		final t = new Trace(s)
		t as EventBModel

		then:
		thrown(ClassCastException)
	}

	def "casting trace with StateSpace works"() {
		when:
		final t = new Trace(s)

		then:
		t.stateSpace == t as StateSpace
	}

	def "casting trace with other kind of class doesn't work"() {
		when:
		final t = new Trace(s)
		t as String

		then:
		thrown(ClassCastException)
	}

	def "there are accessor methods for current and previous states, and for the current transition"() {
		when:
		final t = new Trace(s).$initialise_machine()

		then:
		t.currentState.id == "0"
		t.previousState.id == "root"
		t.currentTransition.name == "\$initialise_machine"
	}

	def "you can view the transitions from the trace (which will not be evaluated by default)"() {
		given:
		final t = new Trace(s).$initialise_machine()

		when:
		final outtrans = t.nextTransitions
		final outtrans2 = t.getNextTransitions(false, FormulaExpand.TRUNCATE) // this is identical to the above call

		then:
		outtrans.size() == outtrans2.size()
		outtrans.every {!it.evaluated}
		outtrans2.every {!it.evaluated}
	}

	def "you can view the transitions from the trace (which can be evaluated)"() {
		given:
		final t = new Trace(s).$initialise_machine()

		when:
		final outtrans = t.getNextTransitions(true, FormulaExpand.EXPAND)

		then:
		outtrans.size() == 4
		outtrans.every {it.evaluated}
	}

	def "the list of transitions can be accessed from the trace"() {
		given:
		final t = new Trace(s).$initialise_machine().new("pp=PID1")

		when:
		final transitions = t.transitionList
		final transitions2 = t.getTransitionList(false, FormulaExpand.TRUNCATE) // identical to other call

		then:
		transitions.collect {it.name} == ["\$initialise_machine", "new"]
		transitions2.collect {it.name} == ["\$initialise_machine", "new"]

		transitions.every {!it.evaluated}
		transitions2.every {!it.evaluated}

	}

	def "the list of transitions can be accessed from the trace (and evaluated at the same time)"() {
		given:
		final t = new Trace(s).$initialise_machine().new("pp=PID1")

		when:
		final transitions = t.getTransitionList(true, FormulaExpand.EXPAND)

		then:
		transitions.collect {it.name} == ["\$initialise_machine", "new"]
		transitions.every {it.evaluated}
	}

	def "A trace can be copied (everything identical except UUID)"() {
		given:
		final t = new Trace(s).$initialise_machine()

		when:
		final t2 = t.copy()

		then:
		t.current == t2.current
		t.head == t2.head
		t.transitionList == t2.transitionList
		t.UUID != t2.UUID
	}
}
