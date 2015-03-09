package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.AbstractEvalResult
import de.prob.scripting.ClassicalBFactory


class StateEvaluationTest extends Specification {

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

	def "it is possible to evaluate a string (will be parsed by model)"() {
		expect:
		secondState.eval("waiting").getValue() == "{PID1}"
	}

	def "it is possible to evaluate an IEvalElement"() {
		expect:
		secondState.eval("waiting" as ClassicalB).getValue() == "{PID1}"
	}

	def "it is possible to evaluate multiple IEvalElements"() {
		expect:
		secondState.eval("waiting" as ClassicalB, "ready" as ClassicalB).collect {it.getValue()} == ["{PID1}", "{}"]
	}

	def "it is possible to evaluate a list of IEvalElements"() {
		expect:
		secondState.eval([
			"waiting" as ClassicalB,
			"ready" as ClassicalB
		]).collect {it.getValue()} == ["{PID1}", "{}"]
	}

	def "if a result is cached, prolog doesn't necessarily have to be contacted"() {
		when:
		IEvalElement blah = "blah" as ClassicalB
		AbstractEvalResult blahres = new EvalResult("blah", [:])
		firstState.values[blah] = blahres

		then:
		firstState.eval([blah]) == [blahres]
		firstState.values.remove(blah)
	}
}
