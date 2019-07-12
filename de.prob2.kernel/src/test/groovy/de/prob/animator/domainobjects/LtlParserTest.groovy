package de.prob.animator.domainobjects

import de.be4.ltl.core.parser.LtlParseException

import spock.lang.Specification

class LtlParserTest extends Specification {
	def "parsing gear(front) = extended with the ClassicalBParser throws an exception"() {
		when:
		new LTL("G {gear(front) = extended}")

		then:
		thrown(LtlParseException)
	}

	def "parsing gear(front) = extended with the EventBParser works"() {
		expect:
		// The != null check is always true; this simply tests that no exception is thrown.
		new LTL("G {gear(front) = extended}", new EventBParserBase()) != null
	}
}
