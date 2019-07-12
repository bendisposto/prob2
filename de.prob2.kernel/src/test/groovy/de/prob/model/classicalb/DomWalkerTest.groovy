package de.prob.model.classicalb

import de.be4.classicalb.core.parser.BParser

import spock.lang.Specification 

class DomWalkerTest extends Specification {
	private ClassicalBMachine machine

	def setup() {
		String testmachine = """
		MACHINE SimplyStructure
		VARIABLES aa, b, Cc
		INVARIANT aa : NAT
		INITIALISATION aa:=1
		CONSTANTS dd, e, Ff
		PROPERTIES dd : NAT
		SETS GGG; Hhh; JJ = {dada, dudu, TUTUT}; iII; kkk = {LLL}
		END
		"""
		final parser = new BParser("testcase")
		final ast = parser.parse(testmachine, false)
		machine = new DomBuilder(null).build(ast)
	}

	def "testing that variables are handled correctly"() {
		expect:
		machine.variables.collect {it.name} == ['aa', 'b', 'Cc']
	}

	def "testing that the name is handled correctly"() {
		expect:
		machine.name == 'SimplyStructure'
	}

	def "test if there are any constants"() {
		expect:
		machine.constants.collect {it.name} == ['dd', 'e', 'Ff']
	}

	def "test if there are any invariants"() {
		expect:
		machine.invariants.collect {it.predicate.code} == ["aa:NAT"]
	}
}
