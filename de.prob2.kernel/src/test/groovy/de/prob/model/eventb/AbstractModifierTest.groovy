package de.prob.model.eventb

import de.prob.model.representation.CSPElement

import spock.lang.Specification 
/**
 *
 * Test some of the util classes that are provided by AbstractModifier
 * @author joy
 *
 */
class AbstractModifierTest extends Specification {
	private AbstractModifier am = new AbstractModifier(Collections.emptySet())

	def "validate properties type cannot be a list with one element"() {
		when:
		def props = am.validateProperties([a: 1], [a: [1]])

		then:
		thrown(IllegalArgumentException)
	}

	def "validate optional property adds it if it isn't there"() {
		when:
		def prop = am.validateOptionalProperty([:],"a", [Integer, 1])

		then:
		prop == ["a", 1]
	}

	def "validate optional property checks type if it is there"() {
		when:
		def prop = am.validateOptionalProperty([a: 2],"a", [Integer, 1])

		then:
		prop == ["a", 2]
	}

	def "validate optional property casts type if necessary"() {
		when:
		def prop = am.validateOptionalProperty([a: [1, 2, 3]],"a", [Set, new HashSet<Integer>()])

		then:
		prop == ["a", [1, 2, 3] as Set]
	}

	def "validate optional property throws exception if bad type casting"() {
		when:
		def prop = am.validateOptionalProperty([a: [1, 2, 3]],"a", [
			Integer,
			new HashSet<Integer>()
		])

		then:
		thrown(ClassCastException)
	}

	def "validate optional property must have tuple with at least two elements"() {
		when:
		def prop = am.validateOptionalProperty([a: [1, 2, 3]],"a", [])

		then:
		thrown(IllegalArgumentException)
	}

	def "validate property throws exception it if it isn't there"() {
		when:
		def prop = am.validateRequiredProperty([:],"a", Integer)

		then:
		thrown(IllegalArgumentException)
	}

	def "validate property checks type if it is there"() {
		when:
		def prop = am.validateRequiredProperty([a: 2],"a", Integer)

		then:
		prop == ["a", 2]
	}

	def "validate property casts type if necessary"() {
		when:
		def prop = am.validateRequiredProperty([a: [1, 2, 3]],"a", Set)

		then:
		prop == ["a", [1, 2, 3] as Set]
	}

	def "validate property throws exception if bad type casting"() {
		when:
		def prop = am.validateRequiredProperty([a: [1, 2, 3]],"a", Integer)

		then:
		thrown(ClassCastException)
	}

	def "helper method to extract definition"() {
		when:
		def defin = am.getDefinition([inv: "x + 1"])

		then:
		defin.label == "inv"
		defin.formula == "x + 1"
	}

	def "definition must have a size of one"() {
		when:
		def defin = am.getDefinition([:])

		then:
		thrown(IllegalArgumentException)
	}

	def "keys and values must be strings"() {
		when:
		def defin = am.getDefinition([a: 1])

		then:
		thrown(IllegalArgumentException)
	}

	def "keys and values must be strings (2)"() {
		when:
		def defin = am.getDefinition([1: "x + 1"])

		then:
		thrown(IllegalArgumentException)
	}

	def "it is possible to validate properties"() {
		when:
		def props = am.validateProperties([a: "x + 1"], [a: String, "extends": [List, []], blah: [String, null]])

		then:
		props["a"] == "x + 1"
		props["extends"] == []
		props["blah"] == null
	}

	def "parsing expression with error formula results in parse exception"() {
		when:
		am.parseExpression("1+")

		then:
		thrown(FormulaParseException)
	}

	def "parsing expression with predicate results in type exception"() {
		when:
		am.parseExpression("1=1")

		then:
		FormulaTypeException ex = thrown()
		ex.getExpected() == "EXPRESSION"
	}

	def elements(String... names) {
		names.collect { new CSPElement(it) }
	}

	def "get unique name that is already unique"() {
		expect:
		am.getUniqueName("unique", elements("inv1","foo","bar","blah","unique2")) == "unique"
	}

	def "prefix element that is not yet unique"() {
		expect:
		am.getUniqueName("a", elements("b","c","a","d")) == "a0"
	}

	def "if a number postfix already exists, increase it if necessary"() {
		expect:
		am.getUniqueName("axm0", elements("axm0","axm4","axm40","axm5")) == "axm41"
	}
}
