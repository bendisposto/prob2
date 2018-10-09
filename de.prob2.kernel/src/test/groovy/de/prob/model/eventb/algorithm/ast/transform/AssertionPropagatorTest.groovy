package de.prob.model.eventb.algorithm.ast.transform

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.graph.NodeNaming
import de.prob.model.representation.ModelElementList
import de.prob.util.Tuple2

import spock.lang.Specification

class AssertionPropagatorTest extends Specification {

	def block(Closure input) {
		new Block().make(input)
	}

	def assertions(Block b) {
		NodeNaming n = new NodeNaming(b)
		AssertionPropagator ap = new AssertionPropagator(new ModelElementList<Procedure>())
		ap.traverse(b)
		ap.assertionMap.collectEntries { Statement stmt, List<Tuple2<List<EventB>,EventB>> v ->
			def formulas = v.collect { Tuple2<List<EventB>,EventB> f ->
				if (f.first.isEmpty()) {
					return f.second.getCode()
				}
				return f.first.collect { it.getCode() }.iterator().join(" & ") + " => ("+f.second.getCode() + ")"
			}
			[n.getName(stmt), formulas]
		}
	}

	def "one assignment"() {
		when:
		def b = block({
			Assign("b := 5")
			Assign("y := x + 1")
			Assert("b = 3 - y")
		})

		then:
		assertions(b) == [assign0: ["5=3 - (x+1)"], assign1: ["b=3 - (x+1)"]]
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
		assertions(b) == [while0: [
				"x > 0 & x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)",
				"x > 0 & not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)"
			],
			if0: [
				"x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)",
				"not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)"
			],
			assign0: ["(p+y0)+x0 / 2*(y0*2)=x*y"],
			assign1: ["p+x0 / 2*(y0*2)=x*y"],
			assign2: ["p+x0*(y0*2)=x*y"]]
	}


	def "while loop with extra predicate"() {
		when:
		def b = block( {
			While("x > 0", invariant: "p + (x0*y0) = x*y") {
				If("x mod 2 /= 0") { Then("p := p + y0") }
				Assign("x0 := x0 / 2")
				Assign("y0 := y0 * 2")
				Assert("p : NAT & x0 : NAT & y0 : NAT")
			}
			Assert("p : NAT & x0 : NAT & y0 : NAT")
		})

		then:

		assertions(b) == [while0: [
				"x > 0 & x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)",
				"x > 0 & x mod 2 /= 0 => (p+y0:NAT&x0 / 2:NAT&y0*2:NAT)",
				"x > 0 & not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)",
				"x > 0 & not(x mod 2 /= 0) => (p:NAT&x0 / 2:NAT&y0*2:NAT)",
				"not(x > 0) => (p : NAT & x0 : NAT & y0 : NAT)"
			],
			if0: [
				"x mod 2 /= 0 => ((p+y0)+x0 / 2*(y0*2)=x*y)",
				"x mod 2 /= 0 => (p+y0:NAT&x0 / 2:NAT&y0*2:NAT)",
				"not(x mod 2 /= 0) => (p+x0 / 2*(y0*2)=x*y)",
				"not(x mod 2 /= 0) => (p:NAT&x0 / 2:NAT&y0*2:NAT)"
			],
			assign0: [
				"(p+y0)+x0 / 2*(y0*2)=x*y",
				"p+y0:NAT&x0 / 2:NAT&y0*2:NAT"
			],
			assign1: [
				"p+x0 / 2*(y0*2)=x*y",
				"p:NAT&x0 / 2:NAT&y0*2:NAT"
			],
			assign2: [
				"p+x0*(y0*2)=x*y",
				"p:NAT&x0:NAT&y0*2:NAT"
			]]
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
		assertions(b) == [while0: [
				"r < n & s < r => (u+v=((s+1)+1)*v)",
				"r < n & not(s < r) => (u=fac(r+1))",
				"not(r < n) => (v = factorial)"
			], while1: [
				"s < r => (u+v=((s+1)+1)*v)",
				"not(s < r) => (u=fac(r+1))"
			], assign0: ["u+v=((s+1)+1)*v"],
			assign1: ["u=((s+1)+1)*v"],
			assign2: ["u=fac(r+1)"],
			assign3: ["v=fac(r+1)"],
			assign4: ["v=fac(r)"]]
	}
}
