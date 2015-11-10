package de.prob.model.eventb

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import org.eventb.core.ast.Assignment
import org.eventb.core.ast.FreeIdentifier

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB

class FormulaUtilTest extends Specification {
	FormulaUtil fuu

	def formulas(String... fs) {
		fs.collect { new EventB(it) }
	}

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

	def "it is possible to abstract Assignment via util method"() {
		expect:
		fuu.getRodinFormula(new EventB("x := 1")) instanceof Assignment
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

	def "getting identifier works"() {
		when:
		def x = fuu.getIdentifier(new EventB("x"))

		then:
		x instanceof FreeIdentifier
	}

	def "getting identifier checks type"() {
		when:
		def x = fuu.getIdentifier(new EventB("x + 1"))

		then:
		thrown PowerAssertionError
	}

	def "getting identifier checks type 2"() {
		when:
		def x = fuu.getIdentifier(new EventB("x < 1"))

		then:
		thrown PowerAssertionError
	}

	def "find formulas which contain an identifier"() {
		when:
		def formulas = fuu.formulasWith(formulas("x := x + 1", "x < 10", "y > 10", "z + 1 + x"), new EventB("x"))

		then:
		formulas.size() == 3
	}

	def "find formulas which contain an identifier y"() {
		when:
		def formulas = fuu.formulasWith(formulas("x := x + 1", "x < 10", "y > 10", "z + 1 + x"), new EventB("y"))

		then:
		formulas.size() == 1
	}

	def "find formulas which contain an identifier z"() {
		when:
		def formulas = fuu.formulasWith(formulas("x := x + 1", "x < 10", "y > 10", "z + 1 + x"), new EventB("z"))

		then:
		formulas.size() == 1
	}

	def "find formulas which contain an identifier m"() {
		when:
		def formulas = fuu.formulasWith(formulas("x := x + 1", "x < 10", "y > 10", "z + 1 + x"), new EventB("m"))

		then:
		formulas.size() == 0
	}

	def "conjuncts to assignments"() {
		when:
		def f = new EventB("res = x / y & rem = x mod y")
		def formula = fuu.conjunctToAssignments(f, ["x", "y"] as Set, ["res", "rem"] as Set)

		then:
		formula[0].getCode() == "res := x / y"
		formula[1].getCode() == "rem := x mod y"
	}

	def "predicates to become such that"() {
		when:
		def f = new EventB("res = x / y & rem = x mod y")
		def formula = fuu.predicateToBecomeSuchThat(f, ["res", "rem"])

		then:
		println formula
	}

	def "apply assignments"() {
		when:
		def f1 = fuu.applyAssignment(new EventB("p + (x0*y0) = x*y"),  new EventB("y0 := y0*2"))
		def f2 = fuu.applyAssignment(f1, new EventB("x0 := x0 / 2"))
		def f3 = fuu.applyAssignment(f2, new EventB("p := p + y0"))

		then:
		f1.getCode() == "p+x0*(y0*2)=x*y"
		f2.getCode() == "p+x0 / 2*(y0*2)=x*y"
		f3.getCode() == "(p+y0)+x0 / 2*(y0*2)=x*y"
	}
}
