package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.AlgorithmPrettyPrinter
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.transform.AssertionExtractor

import spock.lang.Specification

class AssertionExtractorTest extends Specification {

	def run(Closure cls) {
		Block b = new AddLoopEvents().transform(new Block().make(cls).finish())
		[assertions: new AssertionExtractor().extractAssertions(b), algorithm: b]
	}

	def print(e) {
		println new AlgorithmPrettyPrinter(e.algorithm).prettyPrint()
		println e.assertions.toString() + "\n"
	}

	def assertions(e, int index) {
		e.assertions[e.algorithm.statements[index]].collect {
			it.getAssertion().getCode()
		}
	}

	def assertions(e, Statement statement) {
		e.assertions[statement].collect {
			it.getAssertion().getCode()
		}
	}

	def emptyEnd(e) {
		e.algorithm.statements.last() instanceof Skip ? e.algorithm.statements.last() : null
	}

	def "empty is empty"() {
		when:
		def DEBUG = false
		def obj = run({})

		then:
		if (DEBUG) print(obj)
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
	}

	def "one assignment block has two nodes"() {
		when:
		def DEBUG = false
		def obj = run({ Assign("x := 1") })

		then:
		if (DEBUG) print(obj)
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
	}

	def "one assert block has two nodes and one assertion"() {
		when:
		def DEBUG = false
		def obj = run({ Assert("x = 1") })

		then:
		if (DEBUG) print(obj)
		emptyEnd(obj)
		assertions(obj, 1) == ["x = 1"]
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
		assertions(obj, 2) == ["x = 1"]
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
		assertions(obj, 2) == ["x = 1"]
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
		assertions(obj, 2) == ["x = 1"]
		assertions(obj, 4) == ["x >= 10"]
		emptyEnd(obj)
		assertions(obj, emptyEnd(obj)) == ["x = 0"]
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
		assertions(obj, 2) == ["x > 0"]
		assertions(obj, 4) == ["x < 0"]
		emptyEnd(obj)
		assertions(obj, emptyEnd(obj)) == ["x > 0"]
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
		assertions(obj, 2) == ["x = 0"]
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
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
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
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
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
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
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
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
	}

	def "a while with one stmt"() {
		when:
		def DEBUG = false
		def obj = run({
			While("x < 4") { Assign("x := 2") }
		})

		then:
		if (DEBUG) print(obj)
		obj.assertions.each { k,v ->
			v.isEmpty()
		}
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
		assertions(obj, obj.algorithm.statements[0].block.statements[2]) == ["u > v"]
		assertions(obj, emptyEnd(obj)) == ["u|->m|->n : IsGCD"]
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def obj = run({
			While("l /= 1") {
				Assign("l := l / 2")
				Assign("r := r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(obj)
		assertions(obj, emptyEnd(obj)) == ["product = m * n"]
	}

	def "all of the assertions"() {
		when:
		def DEBUG = false
		def obj = run({
			Assert("1 = 1")
			While("u /= v", variant: "u + v") {
				Assert("2 = 2")
				If("u < v") {
					Then {
						Assert("3 = 3")
						Assign("v := v - u")
					}
					Else {
						Assert("4 = 4")
						Assign("u := u - v")
					}
				}
				Assert("5 = 5")
			}
			Assert("m|->n|->v : GCD")//"TRUE = TRUE")
			Assert("6 = 6")
		})
		def while0 = obj.algorithm.statements[1]
		def if0 = while0.block.statements[1]
		def then0 = if0.Then.statements[1]
		def else0 = if0.Else.statements[1]
		def loopToWhile = while0.block.statements[3]

		then:
		if (DEBUG) print(obj)
		assertions(obj, while0) == ["1 = 1"]
		assertions(obj, emptyEnd(obj)) == ["m|->n|->v : GCD", "6 = 6"]
		if0 instanceof If
		assertions(obj, if0) == ["2 = 2"]
		then0 instanceof Assignment
		assertions(obj, then0) == ["3 = 3"]
		else0 instanceof Assignment
		assertions(obj, else0) == ["4 = 4"]
		loopToWhile instanceof Skip
		assertions(obj, loopToWhile) == ["5 = 5"]
	}
}
