package de.prob.model.eventb.algorithm

import spock.lang.Specification

class AlgorithmEqualsAST extends Specification {

	def "assignment equals"() {
		expect:
		new Assignments(["x := 1", "y := 1"]) == new Assignments(["x := 1", "y := 1"])
	}

	def "assert equals"() {
		expect:
		new Assertion("x = 1") == new Assertion("x = 1")
	}
}
