package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.Assignments
import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.eventb.algorithm.ast.transform.AssignmentCombiner
import spock.lang.Specification

class TestAssignmentCombination extends Specification {

	def assignments(Closure c) {
		new AssignmentCombiner().transform(new Block().make(c)).statements.collect { Assignments a ->
			a.assignments.collect { it.getCode() }
		}
	}

	def "these assignments should be combined"() {
		expect:
		assignments({
			Assign("x := 1")
			Assign("y := 2")
			Assign("z := 3")
		}) == [
			[
				"x := 1",
				"y := 2",
				"z := 3"]
		]
	}

	def "if a variable is assigned, it cannot be combined with a following assignment that references it"() {
		expect:
		assignments({
			Assign("x := 5")
			Assign("y := x + 1")
		}) == [["x := 5"], ["y := x + 1"]]
	}
}
