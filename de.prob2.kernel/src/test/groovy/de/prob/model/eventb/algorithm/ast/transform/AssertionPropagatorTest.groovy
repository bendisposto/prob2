package de.prob.model.eventb.algorithm.ast.transform

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.util.Tuple2

class AssertionPropagatorTest extends Specification {

	def block(Closure input) {
		new AssertionPropagator().transform(new Block().make(input))
	}

	def block(String assertion, Closure input) {
		List<Tuple2<List<EventB>, EventB>> toPropagate = [
			new Tuple2<List<EventB>, EventB>([], new EventB(assertion))
		]
		new AssertionPropagator().transformBlock(new Block().make(input), toPropagate)
	}

	def equal(Block input, Closure expected) {
		Block e = new Block().make(expected)
		new ASTEquivalence().assertEqual(input, e)
	}

	def "one assignment"() {
		when:
		def b = block("b = 3 - y", {
			Assign("b := 5")
			Assign("y := x + 1")
		})

		then:
		equal(b, {
			Assert("5=3 - (x+1)")
			Assign("b := 5")
			Assert("b=3 - (x+1)")
			Assign("y := x + 1")
		})
	}

	def "while loop for multiplication"() {
		when:
		def b = block({
			While("x > 0", invariant: "p + (x0*y0) = x*y") {
				If("x mod 2 /= 0") { Then("p := p + y0") }
				Assign("x0 := x0 / 2")
				Assign("y0 := y0 * 2")
			}
		})

		then:
		equal(b, {
			Assert("x > 0 & x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)")
			Assert("x > 0 & not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)")
			While("x > 0", invariant: "p + x0*y0 = x*y") {
				Assert("x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)")
				Assert("not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)")
				If("x mod 2 /= 0") {
					Then {
						Assert("(p+y0)+x0 / 2*(y0*2)=x*y")
						Assign("p := p + y0")
					}
				}
				Assert("p+x0 / 2*(y0*2)=x*y")
				Assign("x0 := x0 / 2")
				Assert("p+x0*(y0*2)=x*y")
				Assign("y0 := y0 * 2")
			}
		})
	}

	def "while loop with extra predicate"() {
		when:
		def b = block("p : NAT & x0 : NAT & y0 : NAT", {
			While("x > 0", invariant: "p + (x0*y0) = x*y") {
				If("x mod 2 /= 0") { Then("p := p + y0") }
				Assign("x0 := x0 / 2")
				Assign("y0 := y0 * 2")
			}
		})

		then:
		equal(b, {
			Assert("x > 0 & x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)")
			Assert("x > 0 & x mod 2 /= 0 => (p+y0:NAT&x0 / 2:NAT&y0*2:NAT)")
			Assert("x > 0 & not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)")
			Assert("x > 0 & not(x mod 2 /= 0) => (p:NAT&x0 / 2:NAT&y0*2:NAT)")
			Assert("not(x > 0) => (p : NAT & x0 : NAT & y0 : NAT)")
			While("x > 0", invariant: "p + x0*y0 = x*y") {
				Assert("x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)")
				Assert("x mod 2 /= 0 => (p+y0:NAT&x0 / 2:NAT&y0*2:NAT)")
				Assert("not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)")
				Assert("not(x mod 2 /= 0) => (p:NAT&x0 / 2:NAT&y0*2:NAT)")
				If("x mod 2 /= 0") {
					Then {
						Assert("(p+y0)+x0 / 2*(y0*2)=x*y")
						Assert("p+y0:NAT&x0 / 2:NAT&y0*2:NAT")
						Assign("p := p + y0")
					}
				}
				Assert("p+x0 / 2*(y0*2)=x*y")
				Assert("p:NAT&x0 / 2:NAT&y0*2:NAT")
				Assign("x0 := x0 / 2")

				Assert("p+x0*(y0*2)=x*y")
				Assert("p:NAT&x0:NAT&y0*2:NAT")
				Assign("y0 := y0 * 2")
			}
		})
	}

	def "factorial example"() {
		when:
		def b = block({
			While("r < n", invariant: "v = fac(r)") {
				While("s < r", invariant: "u = (s + 1) ∗ v") {
					Assign("u := u + v")
					Assign("s := s + 1")
				}
				Assign("v := u")
				Assign("r := r + 1")
				Assign("s := 0")
			}
			Assert("v = factorial")
		})

		then:
		equal(b, {
			Assert("r < n & s < r => (u+v=((s+1)+1)*v)")
			Assert("r < n & s < r => (u+v=fac(r+1))")
			Assert("r < n & not(s < r) => (u=fac(r+1))")
			While("r < n", invariant: "v = fac(r)") {
				Assert("s < r => (u+v=((s+1)+1)*v)")
				Assert("s < r => (u+v=fac(r+1))")
				Assert("not(s < r) => (u=fac(r+1))")
				While("s < r", invariant: "u = (s + 1) ∗ v") {
					Assert("u+v=((s+1)+1)*v")
					Assert("u+v=fac(r+1)")
					Assign("u := u + v")
					Assert("u=((s+1)+1)*v")
					Assert("u=fac(r+1)")
					Assign("s := s + 1")
				}
				Assert("u=fac(r+1)")
				Assign("v := u")
				Assert("v=fac(r+1)")
				Assign("r := r + 1")
				Assert("v=fac(r)")
				Assign("s := 0")
			}
			Assert("v = factorial")
		})
	}
}
