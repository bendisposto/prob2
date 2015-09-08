package de.prob.model.eventb

import spock.lang.Specification

class ContextModifierTest extends Specification {

	ContextModifier modifier

	def setup() {
		def context = new Context("MyContext")
		modifier = new ContextModifier(context)
	}

	def "it is possible to add an enumerated set"() {
		when:
		modifier = modifier.addEnumeratedSet("set1", "x","y","z")
		def context = modifier.getContext()

		then:
		context.sets.size() == 1
		def set = context.sets[0]
		set.getName() == "set1"
		context.constants.size() == 3
		context.constants.collect { it.getName() } == ["x", "y", "z"]
		context.axioms.size() == 1
		context.axioms[0].getPredicate().getCode() == "partition(set1,{x},{y},{z})"
	}

	def "it is possible to remove set elements once added"() {
		when:
		modifier = modifier.addEnumeratedSet("set1", "x","y","z")
		def set = modifier.getContext().sets[0]
		modifier = modifier.removeSet(set)

		then:
		set != null
		modifier.getContext().sets.size() == 0
	}

	def "it is possible to add a carrier set"() {
		when:
		modifier = modifier.set("blah")

		then:
		modifier.getContext().sets[0].getName() == "blah"
	}

	def "it is possible to add a commented set"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.set("blah", mycomment)

		then:
		modifier.getContext().sets.blah.getComment() == mycomment
	}

	def "it is possible to remove a set once added"() {
		when:
		modifier = modifier.set("blah")
		def set = modifier.getContext().sets[0]
		modifier = modifier.removeSet(set)

		then:
		set != null
		modifier.getContext().sets.size() == 0
	}

	def "it is possible to add an axiom"() {
		when:
		modifier = modifier.axiom("TRUE = TRUE")

		then:
		modifier.getContext().axioms[0].getPredicate().getCode() == "TRUE = TRUE"
	}

	def "it is possible to add a commented axiom"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.axiom("axm", "x : NAT", false, mycomment)

		then:
		modifier.getContext().axioms.axm.getComment() == mycomment
	}

	def "it is possible to remove an axiom once added"() {
		when:
		modifier = modifier.axiom("TRUE = TRUE")
		def axiom = modifier.getContext().axioms[0]
		modifier = modifier.removeAxiom(axiom)

		then:
		axiom != null
		modifier.getContext().axioms.size() == 0
	}

	def "it is possible to add a constant"() {
		when:
		modifier = modifier.constant("x")

		then:
		modifier.getContext().constants.size() == 1
		modifier.getContext().constants[0].getName() == "x"
	}

	def "it is possible to add a commented constant"() {
		when:
		def mycomment = "this is a comment"
		def modifier = modifier.constant("x", mycomment)

		then:
		modifier.getContext().constants.x.getComment() == mycomment
	}

	def "it is possible to remove a constant"() {
		when:
		modifier = modifier.constant("x")
		def constant = modifier.getContext().constants[0]
		modifier = modifier.removeConstant(constant)

		then:
		constant != null
		modifier.getContext().constants.size() == 0
	}

	def "names for axiom generation are correct"() {
		when:
		modifier = modifier.axiom(axm4: "1 = 1")
		modifier = modifier.axiom("2 = 2")
		modifier = modifier.axiom(axm10: "3 = 3")
		modifier = modifier.axiom("4 = 4")
		modifier = modifier.axiom("5 = 5")

		then:
		modifier.getContext().axioms.collect { it.getName() } == [
			"axm4",
			"axm5",
			"axm10",
			"axm11",
			"axm12"
		]
	}

	def "parse error for set when inputting invalid formula"() {
		when:
		modifier.set("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for constant when inputting invalid formula"() {
		when:
		modifier.constant("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parse error for axiom when inputting invalid formula"() {
		when:
		modifier.axiom("1+")

		then:
		thrown(FormulaParseException)
	}

	def "type error for constant when inputting predicate"() {
		when:
		modifier.constant("1=1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for constant when inputting expression"() {
		when:
		modifier.constant("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for set when inputting predicate"() {
		when:
		modifier.set("1=1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for set when inputting expression"() {
		when:
		modifier.set("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "IDENTIFIER"
	}

	def "type error for axiom when inputting expression"() {
		when:
		modifier.axiom("1+1")

		then:
		FormulaTypeException e = thrown()
		e.getExpected() == "PREDICATE"
	}
}
