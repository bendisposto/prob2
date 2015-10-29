package de.prob.model.eventb.algorithm

import de.prob.model.eventb.algorithm.ast.Assignments;
import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.eventb.algorithm.ast.transform.AssignmentCombiner
import spock.lang.Specification

class AssignmentConstruction extends Specification {

	def "all are combined"() {
		when:
		def b = new Block().Assign("x := 1","y := 2","z := 3")
		def b2 = new AssignmentCombiner().transform(b)

		then:
		b2.statements.size() == 1
		b2.statements[0].getAssignments().collect { it.getCode() } ==  ["x := 1", "y := 2", "z := 3"]
	}

	def "more complicated"() {
		when:
		def b = new Block().Assign("x := 1","y := 2","z := 3").Assign("x,z :| x=1 & z=5", "y :: {1,2,3}", "a := 3")
		def b2 = new AssignmentCombiner().transform(b)

		then:
		b2.statements.size() == 2
		b2.statements[0].getAssignments().collect { it.getCode() } ==  ["x := 1", "y := 2", "z := 3"]
		b2.statements[1].getAssignments().collect { it.getCode() } == [
			"x,z :| x=1 & z=5",
			"y :: {1,2,3}",
			"a := 3"
		]
	}

	def "via block interface"() {
		when:
		Block b = new Block().Assign("v := v - u").Assign("v,u := u - v,m").Assign("z,m := x,y")
		def b2 = new AssignmentCombiner().transform(b)

		then:
		b2.statements.size() == 2
		b2.statements[0] instanceof Assignments
		b2.statements[1] instanceof Assignments
		b2.statements[0].getAssignments().collect { it.getCode() } == ["v := v - u"]
		b2.statements[1].getAssignments().collect { it.getCode() } == [
			"v,u := u - v,m",
			"z,m := x,y"
		]
	}
}
