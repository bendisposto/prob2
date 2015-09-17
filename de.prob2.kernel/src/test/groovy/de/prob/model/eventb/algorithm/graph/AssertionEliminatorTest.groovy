package de.prob.model.eventb.algorithm.graph

import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.eventb.algorithm.AlgorithmPrettyPrinter
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Statement

public class AssertionEliminatorTest extends Specification {

	def AssertionEliminator run(Closure cls) {
		Block b = new Block().make(cls)
		return new AssertionEliminator(b)
	}

	def print(AssertionEliminator e) {
		println new AlgorithmPrettyPrinter(e.getAlgorithm()).prettyPrint()
		println e.assertions.toString() + "\n"
	}

	def assertions(AssertionEliminator e, int index) {
		e.assertions[e.algorithm.statements[index]].collect {
			it.assertion.getCode()
		}
	}

	def assertions(AssertionEliminator e, Statement statement) {
		e.assertions[statement].collect {
			it.assertion.getCode()
		}
	}

	def emptyEnd(AssertionEliminator e) {
		e.algorithm.statements.last().assignments == []? e.algorithm.statements.last() : null
	}

	def "empty is empty"() {
		when:
		def DEBUG = false
		def obj = run({})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "one assignment block has two nodes"() {
		when:
		def DEBUG = false
		def obj = run({ Assign("x := 1", "y := 1") })

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "one assert block has two nodes and one assertion"() {
		when:
		def DEBUG = false
		def obj = run({ Assert("x = 1") })

		then:
		if (DEBUG) print(obj)
		emptyEnd(obj)
		assertions(obj, 0) == ["x = 1"]
	}

	def "an assert in front of a statement"() {
		when:
		def DEBUG = false
		def obj = run({
			Assign("x := 2")
			Assert("x = 1")
			Assign("x := 3")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, 1) == ["x = 1"]
	}

	def "an assert before and after a while"() {
		when:
		def DEBUG = false
		def obj = run({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, 1) == ["x = 1"]
		emptyEnd(obj)
		assertions(obj, emptyEnd(obj)) == ["x >= 10"]
	}

	def "an assert in between whiles"() {
		when:
		def DEBUG = false
		def obj = run({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, 1) == ["x = 1"]
		assertions(obj, 2) == ["x >= 10"]
		assertions(obj, 3) == ["x = 0"]
		emptyEnd(obj)
	}

	def "an assert between ifs"() {
		when:
		def DEBUG = false
		def obj = run({
			Assign("x := 1")
			Assert("x > 0")
			If("x > 0") {
				Then("x := 0 - x")
				Else("x := x - 1")
			}
			Assert("x < 0")
			If("x < 0") {
				Then("x := 0 - x")
				Else("x := x + 1")
			}
			Assert("x > 0")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, 1) == ["x > 0"]
		assertions(obj, 2) == ["x < 0"]
		assertions(obj, 3) == ["x > 0"]
		emptyEnd(obj)
	}

	def "two decrementers"() {
		when:
		def DEBUG = false
		def obj = run({
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
			While("y > 0") { Assign("y := y - 1") }
			Assert("y = 0")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, 1) == ["x = 0"]
		assertions(obj, emptyEnd(obj)) == ["y = 0"]
	}

	def "an empty if has only one node"() {
		when:
		def DEBUG = false
		def obj = run({
			If("x < 4") {
				Then {}
				Else {}
			}
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "an if with then has 2 nodes"() {
		when:
		def DEBUG = false
		def obj = run({
			If("x < 4") {
				Then("x := 1")
				Else {}
			}
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "an if with else has 3 nodes"() {
		when:
		def DEBUG = false
		def obj = run({
			If("x < 4") {
				Then {}
				Else("x := 1")
			}
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "an empty while has 2 nodes"() {
		when:
		def DEBUG = false
		def obj = run({
			While("x < 4") {
			}
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "a while with one stmt"() {
		when:
		def DEBUG = false
		def obj = run({
			While("x < 4") { Assign("x := 2") }
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.isEmpty()
	}

	def "optimized euclid"() {
		when:
		def DEBUG = false
		def obj = run({
			While("u /= 0") {
				If ("u < v") { Then("u := v", "v := u") }
				Assert("u > v")
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, obj.algorithm.statements[0].block.statements[1]) == ["u > v"]
		assertions(obj, emptyEnd(obj)) == ["u|->m|->n : IsGCD"]
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def obj = run({
			While("l /= 1") {
				Assign("l := l / 2", "r := r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, emptyEnd(obj)) == ["product = m * n"]
	}

	//	def "loop within loop"() {
	//		when:
	//		def DEBUG = true
	//		def obj = run({
	//			While("x < 50") {
	//				If("y > x") {
	//					Then {
	//						While("x < y") { Assign("x := x + 1") }
	//					}
	//				}
	//				Assign("y := y / 2", "x := x / 2")
	//			}
	//			Assign("z := y + x")
	//		})
	//
	//		then:
	//		if (DEBUG) print(obj)
	//	}
}
