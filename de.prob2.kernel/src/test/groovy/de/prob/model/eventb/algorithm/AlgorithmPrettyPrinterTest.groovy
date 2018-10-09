package de.prob.model.eventb.algorithm

import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

import static de.prob.unicode.UnicodeTranslator.toUnicode

class AlgorithmPrettyPrinterTest extends Specification {

	private final DEBUG = false

	def evalAndPrint(Closure definition) {
		def Block b = new Block().make(definition)
		def s = new AlgorithmPrettyPrinter(b).prettyPrint()
		if (DEBUG) {
			println s
			println()
		}
		s
	}

	def "empty while loop"() {
		expect:
		evalAndPrint({
			While("1 > 0") {
				// Do nothing
			}
		}) == "while (1 > 0):\n  // do nothing\n"
	}

	def "simple while loop"() {
		expect:
		evalAndPrint({
			While("x < 1") {
				Assign("y := 2")
				Assign("z := 3")
			}
		}) == "while (x < 1):\n  y \u2254 2\n  z \u2254 3\n"
	}

	def "while loop with variant"() {
		expect:
		evalAndPrint({
			While("x < 1", variant: "0 - x") { Assign("x := x + 1") }
		}) == "while (x < 1):\n  variant: 0 \u2212 x\n  x \u2254 x + 1\n"
	}

	def "pretty print euclid"() {
		expect:
		evalAndPrint({
			While("u /= 0") {
				If("u < v") { Then("u := v", "v := u") }
				Assign("u := u - v")
			}
			Assert("v|->m|->n : IsGCD")
		}) == "while (u \u2260 0):\n  if (u < v):\n    u \u2254 v\n    v \u2254 u\n  u \u2254 u \u2212 v\nassert v\u21A6m\u21A6n \u2208 IsGCD\n"
	}

	def "if with else"() {
		expect:
		evalAndPrint({
			If("x < y") {
				Then("z := x")
				Else("z := y")
			}
		}) == "if (x < y):\n  "+toUnicode("z := x")+"\nelse:\n  "+toUnicode("z := y")+"\n"
	}
}
