package de.prob.model.eventb.algorithm

import de.be4.classicalb.core.parser.node.Node
import de.prob.animator.domainobjects.EventB

import spock.lang.Specification

class AssignmentIndependenceTest extends Specification {

	def identifiers(String assignment) {
		Node ast = new EventB(assignment).getAst()
		IdentifierExtractor v = new IdentifierExtractor()
		ast.apply(v)
		v.getIdentifiers()
	}

	def "deterministic assignment"() {
		expect:
		identifiers("x := 1") == ["x"] as Set
	}

	def "deterministic assignment function calls"() {
		expect:
		identifiers("f(x) := 1") == ["f", "x"] as Set
	}

	def "multiple deterministic assignment"() {
		expect:
		identifiers("x,y,z := 1,2,3")== ["x", "y", "z"] as Set
	}

	def "nondeterministic assignment"() {
		expect:
		identifiers("x :: {1,2,3}")== ["x"] as Set
	}

	def "become such that"() {
		expect:
		identifiers("x,y :| x = 1 & y = 2") == ["x", "y"] as Set
	}

	def "union"() {
		expect:
		IdentifierExtractor.union(new EventB("x,y,z := 1,2,3").getAst(),
				new EventB("a,b :| a=1 & b=4").getAst(),
				new EventB("y :: {1,2,3}").getAst()) == ["x", "y", "z", "a", "b"] as Set
	}

	def "intersection"() {
		expect:
		IdentifierExtractor.intersection(new EventB("x,y,z := 1,2,3").getAst(),
				new EventB("a,b :| a=1 & b=4").getAst()) == [] as Set
	}


	def "intersection 2"() {
		expect:
		IdentifierExtractor.intersection(new EventB("x,y,z := 1,2,3").getAst(),
				new EventB("y :: {1,2,3}").getAst()) == ["y"] as Set
	}

	def "disjoint"() {
		expect:
		IdentifierExtractor.disjoint(new EventB("x,y,z := 1,2,3").getAst(),
				new EventB("a,b :| a=1 & b=4").getAst())
	}


	def "disjoint 2"() {
		expect:
		!IdentifierExtractor.disjoint(new EventB("x,y,z := 1,2,3").getAst(),
				new EventB("y :: {1,2,3}").getAst())
	}
}
