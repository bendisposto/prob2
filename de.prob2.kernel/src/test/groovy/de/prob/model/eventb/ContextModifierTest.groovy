package de.prob.model.eventb

import de.prob.model.representation.ElementComment

import org.eventb.core.ast.extension.IFormulaExtension

import spock.lang.Specification

class ContextModifierTest extends Specification {
	private ContextModifier modifier

	def setup() {
		def context = new Context("MyContext")
		modifier = new ContextModifier(context)
	}

	def "context in constructor cannot be null"() {
		when:
		new ContextModifier(null)

		then:
		thrown IllegalArgumentException
	}

	def "null type environment will result in empty set"() {
		when:
		def cm = new ContextModifier(new Context("name"), null)

		then:
		cm.typeEnvironment == [] as Set
	}

	def "typeEnv can be set"() {
		when:
		def ctx = new Context("name")
		def typeEnv = [Mock(IFormulaExtension)] as Set
		def cm = new ContextModifier(ctx, typeEnv)

		then:
		cm.typeEnvironment == typeEnv
		cm.getContext() == ctx
	}

	def "it is possible to add an extended context"() {
		when:
		Context ctx = new Context("EE")
		modifier = modifier.setExtends(ctx)

		then:
		modifier.getContext().getExtends() == [ctx]
	}

	def "setExtends cannot be null"() {
		when:
		modifier.setExtends(null)

		then:
		thrown IllegalArgumentException
	}

	def "it is possible to add an enumerated set (from map)"() {
		when:
		modifier = modifier.enumerated_set name: "set1",
		constants: ["x", "y", "z"]
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

	def "enumerated_set requires name"() {
		when:
		modifier = modifier.enumerated_set constants: ["x", "y", "z"]

		then:
		thrown IllegalArgumentException
	}

	def "enumerated_set requires constants"() {
		when:
		modifier = modifier.enumerated_set name: "set1"

		then:
		thrown IllegalArgumentException
	}

	def "enumerated_set cannot be null"() {
		when:
		modifier.enumerated_set(null)

		then:
		thrown IllegalArgumentException
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

	def "it is possible to remove a set via name once added"() {
		when:
		modifier = modifier.addEnumeratedSet("set1", "x","y","z")
		def set = modifier.getContext().sets.set1
		modifier = modifier.removeSet("set1")

		then:
		set != null
		modifier.getContext().sets.size() == 0
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

	def "addEnumeratedSet: set name cannot be null"() {
		when:
		modifier.addEnumeratedSet(null)

		then:
		thrown IllegalArgumentException
	}

	def "addEnumeratedSet: elements cannot be null"() {
		when:
		modifier.addEnumeratedSet("X", null)

		then:
		thrown IllegalArgumentException
	}

	def "addEnumeratedSet: multiple elements cannot be null"() {
		when:
		modifier.addEnumeratedSet("X", "x", null, "a", null, "b")

		then:
		thrown IllegalArgumentException
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

	def "set cannot be null"() {
		when:
		modifier.set(null)

		then:
		thrown IllegalArgumentException
	}

	def "null comment for set results in empty comment"() {
		when:
		modifier = modifier.set("MySet", null)

		then:
		modifier.getContext().sets.MySet.getComment() == ""
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

	def "if a set doesn't exist, or null nothing happens with removeSet"() {
		when:
		def modifier1 = modifier.removeSet("DontExist")
		def modifier2 = modifier.removeSet(null)

		then:
		modifier1 == modifier
		modifier2 == modifier
	}

	def "it is possible to add multiple axioms"() {
		when:
		modifier = modifier.axioms axm1: "cst + 1 = 2",
		axm2: "1 = 2",
		axm4: "life : GREATNESS"
		def axms = modifier.getContext().axioms

		then:
		axms.collect { it.getName() } == ["axm1", "axm2", "axm4"]
		axms.collect { it.getPredicate().getCode() } == [
			"cst + 1 = 2",
			"1 = 2",
			"life : GREATNESS"
		]
	}

	def "it is possible to add multiple axioms from list"() {
		when:
		modifier = modifier.axioms "cst + 1 = 2",
				"1 = 2",
				"life : GREATNESS"
		def axms = modifier.getContext().axioms

		then:
		axms.collect { it.getName() } == ["axm0", "axm1", "axm2"]
		axms.collect { it.getPredicate().getCode() } == [
			"cst + 1 = 2",
			"1 = 2",
			"life : GREATNESS"
		]
	}

	def "axioms cannot be null"() {
		when:
		modifier = modifier.axioms(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple axioms cannot be null"() {
		when:
		modifier = modifier.axioms("x < 4", null, "x + y = 9", null)

		then:
		thrown IllegalArgumentException
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

	def "it is possible to remove an axiom via name once added"() {
		when:
		modifier = modifier.axiom("axm", "TRUE = TRUE")
		def axiom = modifier.getContext().axioms.axm
		modifier = modifier.removeAxiom("axm")

		then:
		axiom != null
		modifier.getContext().axioms.size() == 0
	}

	def "removing an Axiom that doesn't exist or is null does nothing"() {
		when:
		def idontexist = modifier.removeAxiom("IDontExist")
		def iamnull = modifier.removeAxiom(null)

		then:
		modifier == idontexist
		modifier == iamnull
	}

	def "it is possible to add a theorem"() {
		when:
		modifier = modifier.theorem("TRUE = TRUE")
		def axiom = modifier.getContext().axioms[0]

		then:
		axiom.getPredicate().getCode() == "TRUE = TRUE"
		axiom.isTheorem()
	}

	def "it is possible to add a theorem from map"() {
		when:
		modifier = modifier.theorem thm: "TRUE = TRUE"
		def thm = modifier.getContext().axioms.thm

		then:
		thm.getPredicate().getCode() == "TRUE = TRUE"
		thm.isTheorem()
	}

	def "theorem cannot be null"() {
		when:
		modifier.theorem(null)

		then:
		thrown IllegalArgumentException
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

	def "constant cannot be null"() {
		when:
		modifier.constant(null)

		then:
		thrown IllegalArgumentException
	}

	def "constant null comment results in empty comment"() {
		when:
		modifier = modifier.constant("c", null)

		then:
		modifier.getContext().constants.c.getComment() == ""
	}

	def "it is possible to add multiple constants"() {
		when:
		modifier = modifier.constants "x", "y", "z"

		then:
		modifier.getContext().constants.collect { it.getName() } == ["x", "y", "z"]
	}

	def "constants cannot be null"() {
		when:
		modifier.constants(null)

		then:
		thrown IllegalArgumentException
	}

	def "multiple constants cannot be null"() {
		when:
		modifier.constants("a",null,"b",null,"c")

		then:
		thrown IllegalArgumentException
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

	def "it is possible to remove a constant via name"() {
		when:
		modifier = modifier.constant("x")
		def constant = modifier.getContext().constants.x
		modifier = modifier.removeConstant("x")

		then:
		constant != null
		modifier.getContext().constants.size() == 0
	}

	def "removeConstant for nonexistant constant or null does nothing"() {
		when:
		def nonexistant = modifier.removeConstant("IDontExist")
		def iamnull = modifier.removeConstant(null)

		then:
		modifier == nonexistant
		modifier == iamnull
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
			"axm0",
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

	def "it is possible to add a comment"() {
		when:
		String mycomment = "This is a comment!"
		modifier = modifier.addComment(mycomment)

		then:
		modifier.getContext().getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "adding a null or empty comment does nothing"() {
		when:
		def iamnull = modifier.addComment(null)
		def iamempty = modifier.addComment("")

		then:
		modifier == iamnull
		modifier == iamempty
	}

	def "definition for make cannot be null"() {
		when:
		modifier.make(null)

		then:
		thrown IllegalArgumentException
	}

	def 'make method works'() {
		when:
		modifier = modifier.make {
			enumerated_set name: "x",
			constants: ["a", "b", "c"]
			axiom axm: "card(x) = 3"
		}
		def ctx = modifier.getContext()

		then:
		ctx.sets.collect { it.getName() } == ["x"]
		ctx.constants.collect { it.getName() } == ["a", "b", "c"]
		ctx.axioms.collect { it.getName() } == ["axm0", "axm"]
		ctx.axioms.collect { it.getPredicate().getCode() } == [
			"partition(x,{a},{b},{c})",
			"card(x) = 3"
		]
	}
}
