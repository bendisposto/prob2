package de.prob.model.eventb.algorithm

import de.prob.model.eventb.algorithm.ast.If

import spock.lang.Specification

class IfConstruction extends Specification {
	def "it is possible to construct a simple if"() {
		expect: new If("TRUE = TRUE").finish() != null
	}

	def "it is possible to construct an if with a closure definition"() {
		when:
		If s = new If("TRUE = TRUE").make {}

		then:
		s.Then.statements.isEmpty()
		s.Else.statements.isEmpty()
	}

	def "it is possible to construct an if with assignments"() {
		when:
		If s = new If("TRUE = FALSE").make {
			Then("x := 2", "y := 5", "z := 4")
			Else("x := 5")
		}
		then:
		s.Then.statements.collect { it.assignment.getCode() } == ["x := 2", "y := 5", "z := 4"]
		s.Else.statements.collect { it.assignment.getCode() } == ["x := 5"]
	}

	def "it is possible to construct an If with closures"() {
		when:
		If s = new If("TRUE = TRUE").make {
			Then {
				If("x = 1") {
					Then("x := 2")
					Else("x := 3")
				}
			}
			Else  {
				If("x = 5") {
					Then("x := 5")
					Else("x := 4")
				}
			}
		}
		then:
		s.Then.statements[0] instanceof If
		s.Else.statements[0] instanceof If
	}
}
