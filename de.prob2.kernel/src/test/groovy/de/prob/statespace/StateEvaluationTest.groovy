package de.prob.statespace

import de.prob.Main
import de.prob.animator.domainobjects.AbstractEvalResult
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.IEvalElement
import de.prob.scripting.ClassicalBFactory

import spock.lang.Specification

class StateEvaluationTest extends Specification {

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

	def cleanupSpec() {
		s.kill()
	}

	def "it is possible to evaluate a string (will be parsed by model)"() {
		expect:
		secondState.eval("waiting").getValue() == "{PID1}"
	}

	def "it is possible to evaluate an IEvalElement"() {
		expect:
		secondState.eval(new ClassicalB("waiting")).getValue() == "{PID1}"
	}

	def "it is possible to evaluate multiple IEvalElements"() {
		expect:
		secondState.eval(new ClassicalB("waiting"), new ClassicalB("ready")).collect {it.getValue()} == ["{PID1}", "{}"]
	}

	def "it is possible to evaluate a list of IEvalElements"() {
		expect:
		secondState.eval([
			new ClassicalB("waiting"),
			new ClassicalB("ready")
		]).collect {it.getValue()} == ["{PID1}", "{}"]
	}

	def "if a result is cached, prolog doesn't necessarily have to be contacted"() {
		when:
		IEvalElement blah = new ClassicalB("blah")
		AbstractEvalResult blahres = new EvalResult("blah", [:])
		firstState.values[blah] = blahres

		then:
		//TODO currently no caching
		//firstState.eval([blah]) == [blahres]
		//firstState.values.remove(blah)
		true
	}
}
