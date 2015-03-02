package de.prob.statespace

import spock.lang.Specification
import de.prob.Main
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel
import de.prob.scripting.ClassicalBFactory

class TraceConstructionTest extends Specification {

	static AbstractModel m

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		m = factory.load(path)
	}

	def "can create Trace from model"() {
		expect: new Trace(m) != null
	}

	def "can create Trace from StateSpace"() {
		expect: new Trace(m.getStateSpace()) != null
	}

	def "cannot create Trace with null parameter"() {
		when:
		new Trace(null)

		then:
		def e = thrown(GroovyRuntimeException)
		e.getMessage().startsWith("Ambiguous method overloading");
	}

	def "casting trace with AbstractModel works"() {
		when:
		Trace t = new Trace(m)

		then:
		t.getModel() == t as AbstractModel
	}

	def "casting trace with ClassicalBModel works if it is a classical b model"() {
		when:
		Trace t = new Trace(m)

		then:
		t.getModel() == t as ClassicalBModel
	}

	def "casting trace with other model type (i.e. EventB) results in error"() {
		when:
		Trace t = new Trace(m)
		t as EventBModel

		then:
		thrown(ClassCastException)
	}

	def "casting trace with StateSpace works"() {
		when:
		Trace t = new Trace(m)

		then:
		t.getStateSpace() == t as StateSpace
	}

	def "casting trace with ArrayList works (and returns transition list)"() {
		when:
		Trace t = new Trace(m)

		then:
		t.transitionList == t as ArrayList
	}
	

	def "casting trace with other kind of class doesn't work"() {
		when:
		Trace t = new Trace(m)
		t as String

		then:
		thrown(ClassCastException)
	}

	def "there are accessor methods for current and previous states, and for the current transition"() {
		when:
		Trace t = new Trace(m).$initialise_machine()

		then:
		t.getCurrentState().getId() == "0"
		t.getPreviousState().getId() == "root"
		t.getCurrentTransition().getName() == "\$initialise_machine"
	}

	def "you can view the transitions from the trace (which will not be evaluated by default)"() {
		when:
		Trace t = new Trace(m).$initialise_machine()
		def outtrans = t.getNextTransitions()
		def outtrans2 = t.getNextTransitions(false) // this is identical to the above call

		then:
		outtrans.size() == outtrans2.size()
		outtrans.inject(true) { result, i -> !i.isEvaluated() } // all not evaluated
		outtrans2.inject(true) { result, i -> !i.isEvaluated() } // all not evaluated
	}

	def "you can view the transitions from the trace (which can be evaluated)"() {
		when:
		Trace t = new Trace(m).$initialise_machine()
		def outtrans = t.getNextTransitions(true)

		then:
		outtrans.size() == 4
		outtrans.inject(true) { result, i -> result && i.isEvaluated() } // they are all evaluated
	}

	def "the list of transitions can be accessed from the trace"() {
		when:
		Trace t = new Trace(m).$initialise_machine().new("pp=PID1")
		def transitions = t.getTransitionList()
		def transitions2 = t.getTransitionList(false) // identical to other call

		then:
		transitions.collect { it.getName() } == [
			"\$initialise_machine",
			"new"
		]
		transitions2.collect { it.getName() } == [
			"\$initialise_machine",
			"new"
		]

		transitions.inject(true) { result, i -> !i.isEvaluated() } // all not evaluated
		transitions2.inject(true) { result, i -> !i.isEvaluated() } // all not evaluated

	}

	def "the list of transitions can be accessed from the trace (and evaluated at the same time)"() {
		when:
		Trace t = new Trace(m).$initialise_machine().new("pp=PID1")
		def transitions = t.getTransitionList(true)

		then:
		transitions.collect { it.getName() } == [
			"\$initialise_machine",
			"new"
		]
		transitions.inject(true) { result, i -> result && i.isEvaluated() } // they are all evaluated
	}

	def "A trace can be copied (everything identical except UUID)"() {
		when:
		Trace t = new Trace(m).$initialise_machine()
		Trace t2 = t.copy()

		then:
		t.current == t2.current
		t.head == t2.head
		t.transitionList == t2.transitionList
		!t.transitionList.is(t2.transitionList)
		t.getUUID() != t2.getUUID()
	}
}
