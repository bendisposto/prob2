package de.prob.model.eventb

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB

class FormulaUtilTest extends Specification {
	FormulaUtil fuu

	def setup() {
		fuu = new FormulaUtil()
	}

	def "substitutions work for predicates"() {
		when:
		def e = fuu.substitute(new EventB("product = x * y"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "p=x0*y0"
	}


	def "substitutions work for expressions"() {
		when:
		def e = fuu.substitute(new EventB("x * f(y)"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "x0*f(y0)"
	}

	def "substitutions work for simple assignments"() {
		when:
		def e = fuu.substitute(new EventB("product := x * y"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "p:=x0*y0"
	}

	def "substitutions work for multiple deterministic assignments"() {
		when:
		def e = fuu.substitute(new EventB("product,x,y := x * y,y,x"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "p,x0,y0:=x0*y0,y0,x0"
	}

	def "substitutions work for become such that"() {
		when:
		def e = fuu.substitute(new EventB("product :| product' = x * y"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "p:|p'=x0*y0"
	}

	def "substitutions work for become element of"() {
		when:
		def e = fuu.substitute(new EventB("x :: 1..x"), [product: new EventB("p"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		e.getCode() == "x0::1 .. x0"
	}

	def "substitutions have to be expressions"() {
		when:
		fuu.substitute(new EventB("x :: 1..x"), [product: new EventB("p<1"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		thrown IllegalArgumentException
	}

	def "substitutionAssignment must be an assignment"() {
		when:
		fuu.substitute(new EventB("x+1"), [product: new EventB("p<1"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		thrown IllegalArgumentException
	}

	def "substitutionDeterministicAssignment must be a deterministic assignment"() {
		when:
		fuu.substitute(new EventB("x::NAT"), [product: new EventB("p<1"), x: new EventB("x0"), y: new EventB("y0")])

		then:
		thrown IllegalArgumentException
	}
}
