package de.prob.model.eventb.algorithm

import spock.lang.Specification

class AssignmentConstruction extends Specification {

	def "disjoint returns one element"() {
		when:
		def a = new Assignments().addAssignments("x := 1","y := 2","z := 3")

		then:
		a.size() == 1
		a[0].getAssignments().collect { it.getCode() } ==  ["x := 1", "y := 2", "z := 3"]
	}

	def "more complicated"() {
		when:
		def a = new Assignments().addAssignments("x := 1","y := 2","z := 3")
		def b = a[0].addAssignments("x,z :| x=1 & z=5", "y :: {1,2,3}", "a := 3")

		then:
		b.size() == 2
		b[0].getAssignments().collect { it.getCode() } ==  ["x := 1", "y := 2", "z := 3"]
		b[1].getAssignments().collect { it.getCode() } == [
			"x,z :| x=1 & z=5",
			"y :: {1,2,3}",
			"a := 3"
		]
	}

	def "via block interface"() {
		when:
		Block b = new Block().Assign("v := v - u").Assign("v,u := u - v,m").Assign("z,m := x,y")

		then:
		b.statements.size() == 2
		b.statements[0] instanceof Assignments
		b.statements[1] instanceof Assignments
		b.statements[0].getAssignments().collect { it.getCode() } == ["v := v - u"]
		b.statements[1].getAssignments().collect { it.getCode() } == [
			"v,u := u - v,m",
			"z,m := x,y"
		]
	}
}
