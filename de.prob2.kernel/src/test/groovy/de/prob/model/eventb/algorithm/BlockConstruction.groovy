package de.prob.model.eventb.algorithm

import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.While

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
		def b = new Block().make {
			Assign("x := 1")
			Assign("y := 2")
		}
		then:
		b.statements.size() == 2
		b.statements[0] instanceof Assignment
		b.statements[0].assignment.getCode() == "x := 1"
		b.statements[1] instanceof Assignment
		b.statements[1].assignment.getCode() == "y := 2"
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

	def "it is possible to create If without closure"() {
		when:
		def b = new If("x < 5").Then(new Block().Assign("x := 2").Assign("y := 1")).Else(new Block().Assign("x := 1").Assign("y := 2"))
		then:
		b instanceof If
		b.Then.statements[0] instanceof Assignment
		b.Then.statements[1] instanceof Assignment
		b.Else.statements[0] instanceof Assignment
		b.Else.statements[1] instanceof Assignment
	}

	def "it is possible to create a While without a closure"() {
		when:
		def b = new Block().While("x < 5", new Block().Assign("x := 2").Assign("y := 3"))

		then:
		b.statements[0] instanceof While
		b.statements[0].block.statements[0] instanceof Assignment
		b.statements[0].block.statements[1] instanceof Assignment
	}

	def "it is possible to create a While with invariant and variant without a closure"() {
		when:
		def b = new Block().While("x < 5", new Block().Assign("x := 2").Assign("y := 3"),"x < 5", "x + 5")

		then:
		b.statements[0] instanceof While
		b.statements[0].invariant.getCode() == "x < 5"
		b.statements[0].variant.getCode() == "x + 5"
		b.statements[0].block.statements[0] instanceof Assignment
		b.statements[0].block.statements[1] instanceof Assignment
	}
}
