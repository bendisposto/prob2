package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.Block

import spock.lang.Specification

class NamingNodesTest extends Specification {

	def Set<String> name(Closure cls) {
		Block b = new Block().make(cls)
		return new NodeNaming(b).getNodes().keySet()
	}

	def print(nodes) {
		println nodes
	}

	def "empty is empty"() {
		when:
		def DEBUG = false
		def names = name({})

		then:
		if (DEBUG) print(names)
		names.isEmpty()
	}

	def "one assignment block has two nodes"() {
		when:
		def DEBUG = false
		def names = name({ Assign("x := 1") })

		then:
		if (DEBUG) print(names)
		names == ["assign0"] as Set
	}

	def "one assert block has two nodes and one assertion"() {
		when:
		def DEBUG = false
		def names = name({ Assert("x = 1") })

		then:
		if (DEBUG) print(names)
		names == ["assert0"] as Set
	}

	def "an assert in front of a statement"() {
		when:
		def DEBUG = false
		def names = name({
			Assign("x := 2")
			Assert("x = 1")
			Assign("x := 3")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"assign0",
			"assert0",
			"assign1"] as Set
	}

	def "an assert before and after a while"() {
		when:
		def DEBUG = false
		def names = name({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"assign0",
			"assert0",
			"while0",
			"assign1",
			"assert1"] as Set
	}

	def "an assert in between whiles"() {
		when:
		def DEBUG = false
		def names = name({
			Assign("x := 1")
			Assert("x = 1")
			While("x < 10") { Assign("x := x + 1") }
			Assert("x >= 10")
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"assign0",
			"assert0",
			"while0",
			"assign1",
			"assert1",
			"while1",
			"assign2",
			"assert2"] as Set
	}

	def "an assert between ifs"() {
		when:
		def DEBUG = false
		def names = name({
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
		if (DEBUG) print(names)
		names == [
			"assign0",
			"assert0",
			"if0",
			"assign1",
			"assign2",
			"assert1",
			"if1",
			"assign3",
			"assign4",
			"assert2"] as Set
	}

	def "two decrementers"() {
		when:
		def DEBUG = false
		def names = name({
			While("x > 0") { Assign("x := x - 1") }
			Assert("x = 0")
			While("y > 0") { Assign("y := y - 1") }
			Assert("y = 0")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"while0",
			"assign0",
			"assert0",
			"while1",
			"assign1",
			"assert1"] as Set
	}

	def "an empty if has only one node"() {
		when:
		def DEBUG = false
		def names = name({
			If("x < 4") {
				Then {}
				Else {}
			}
		})

		then:
		if (DEBUG) print(names)
		names == ["if0"] as Set
	}

	def "an empty while has 2 nodes"() {
		when:
		def DEBUG = false
		def names = name({
			While("x < 4") {
			}
		})

		then:
		if (DEBUG) print(names)
		names == ["while0"] as Set
	}

	def "a while with one stmt"() {
		when:
		def DEBUG = false
		def names = name({
			While("x < 4") { Assign("x := 2") }
		})

		then:
		if (DEBUG) print(names)
		names == ["while0", "assign0"] as Set
	}

	def "optimized euclid"() {
		when:
		def DEBUG = false
		def names = name({
			While("u /= 0") {
				If ("u < v") { Then("u,v := v,u") }
				Assert("u > v")
				Assign("u := u - v")
			}
			Assert("u|->m|->n : IsGCD")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"while0",
			"if0",
			"assign0",
			"assert0",
			"assign1",
			"assert1"] as Set
	}

	def "russische bauernmultiplikation"(){
		when:
		def DEBUG = false
		def names = name({
			While("l /= 1") {
				Assign("l,r := l / 2, r * 2")
				If("l mod 2 /= 0") { Then("product := product + r") }
			}
			Assert("product = m * n")
		})

		then:
		if (DEBUG) print(names)
		names == [
			"while0",
			"assign0",
			"if0",
			"assign1",
			"assert0"] as Set
	}

	//	def "loop within loop"() {
	//		when:
	//		def DEBUG = false
	//		def names = name({
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
	//		if (DEBUG) print(names)
	//	}
}
