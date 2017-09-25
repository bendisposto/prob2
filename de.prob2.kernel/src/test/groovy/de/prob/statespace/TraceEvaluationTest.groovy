package de.prob.statespace

import de.prob.Main
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.ComputationNotCompletedResult
import de.prob.animator.domainobjects.EnumerationWarning
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.EventB
import de.prob.animator.domainobjects.IdentifierNotInitialised
import de.prob.animator.domainobjects.WDError
import de.prob.scripting.ClassicalBFactory
import de.prob.scripting.EventBFactory

import spock.lang.Specification

class TraceEvaluationTest extends Specification {

	static StateSpace s
	Trace t

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
	}

	def cleanupSpec() {
		s.kill()
	}

	def setup() {
		t = new Trace(s)
	}

	def "when not initialised the result is IdentifierNotInitialised"() {
		when:
		def x = t.evalCurrent("waiting")

		then:
		x instanceof IdentifierNotInitialised
	}

	def "evaluating infinite sets results in enumeration warning"() {
		when:
		def x = t.evalCurrent("card({x|x : NATURAL & x mod 2 = 0})")

		then:
		x instanceof EnumerationWarning
	}

	def "evaluating formulas with well-definedness problems results in WDError"() {
		when:
		def x = t.evalCurrent("1 / 0")

		then:
		x instanceof WDError
	}

	def "evaluating formulas with type issues results in ComputationNotCompleted"() {
		when:
		def x = t.evalCurrent("1 + {}")

		then:
		x instanceof ComputationNotCompletedResult
	}

	def "It is possible to evaluate (correct) formulas in the current state (if initialised)"() {
		when:
		def Trace t = t.$initialise_machine()
		def x = t.evalCurrent("x = waiting & y = card(x)")

		then:
		x instanceof EvalResult
		x.getValue() == "TRUE"
		x.getSolutions().get("x") == "{}"
		x.getSolutions().get("y") == "0"
	}

	def "It is possible to evaluate a formula over the course of a Trace"() {
		when:
		def Trace t = t.$initialise_machine().new("pp = PID1").new("pp = PID2")
		def x = t.eval("waiting")

		then:
		x.size() == 3
		def sIds = t.getTransitionList().collect { it.getDestination().getId() }
		def results = x.collect { [it.getFirst(), it.getSecond().getValue()]}

		results[0] == [sIds[0], "{}"]
		results[1] == [sIds[1], "{PID1}"]
		results[2] == [sIds[2], "{PID1,PID2}"]
	}

	def "It is possible to evaluate a parsed formula over the course of a Trace"() {
		when:
		def Trace t = t.$initialise_machine().new("pp = PID1").new("pp = PID2")
		def x = t.eval(new ClassicalB("waiting"))

		then:
		x.size() == 3
		def sIds = t.getTransitionList().collect { it.getDestination().getId() }
		def results = x.collect { [it.getFirst(), it.getSecond().getValue()]}

		results[0] == [sIds[0], "{}"]
		results[1] == [sIds[1], "{PID1}"]
		results[2] == [sIds[2], "{PID1,PID2}"]
	}

	def "Evaluating a formula over a trace also works for EventB"() {
		when:
		def path = System.getProperties().get("user.dir")+"/groovyTests/Lift/lift0.bcm"
		EventBFactory factory = Main.getInjector().getInstance(EventBFactory.class)
		StateSpace s = factory.extract(path).load([:])
		t = new Trace(s)
		def Trace t = t.$setup_constants().$initialise_machine().up()
		def x = t.eval(new EventB("level"))

		then:
		x.size() == 2
		x[0].getSecond().getValue() == "L0"
		x[1].getSecond().getValue() == "L1"

		cleanup:
		if (s != null) {
			s.kill()
		}
	}
}
