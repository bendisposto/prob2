package de.prob.animator.domainobjects

import spock.lang.Specification
import de.be4.ltl.core.parser.LtlParseException

class LtlParserTest extends Specification {

	def "parsing gear(front) = extended with the ClassicalBParser throws an exception"() {
		when:
		new LTL("G {gear(front) = extended}")

		then:
		thrown LtlParseException
	}

	def "parsing gear(front) = extended with the EventBParser works"() {
		when:
		LTL ltl = new LTL("G {gear(front) = extended}", new EventBParserBase())

		then:
		ltl != null
	}
}
