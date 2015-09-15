package de.prob.model.eventb.algorithm

import spock.lang.Specification

class BlockConstruction extends Specification {

	def "it is possible to create a while loop"() {
		when:
		def b = new Block().make { While("x = 1") { } }
		then:
		b.statements[0] instanceof While
		b.statements[0].condition.getCode() == "x = 1"
		b.statements[0].block.statements.isEmpty()
	}

	def "it is possible to create assignments"() {
		when:
		def b = new Block().make { Assign("x := 1", "y := 2") }
		then:
		b.statements[0] instanceof Assignments
		b.statements[0].assignments.collect { it.getCode() } == ["x := 1", "y := 2"]
	}

	def "it is possible to create assertions"() {
		when:
		def b = new Block().make { Assert("x = 5") }
		then:
		b.statements[0] instanceof Assertion
		b.statements[0].assertion.getCode() == "x = 5"
	}

	def "it is possible to create If statements"() {
		when:
		def b = new Block().make {
			If("x = 4") {
				Then {}
				Else { Assign("x := 5") }
			}
		}
		then:
		b.statements[0] instanceof If
	}
}
