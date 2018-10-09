package de.prob.model.eventb.algorithm

import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

class AssignmentConstruction extends Specification {

	def "via block interface"() {
		when:
		Block b = new Block().Assign("v := v - u").Assign("v,u := u - v,m").Assign("z,m := x,y")

		then:
		b.statements.size() == 3
		b.statements[0] instanceof Assignment
		b.statements[1] instanceof Assignment
		b.statements[0].getAssignment().getCode()  == "v := v - u"
		b.statements[1].getAssignment().getCode()  == "v,u := u - v,m"
		b.statements[2].getAssignment().getCode()  == "z,m := x,y"
	}
}
