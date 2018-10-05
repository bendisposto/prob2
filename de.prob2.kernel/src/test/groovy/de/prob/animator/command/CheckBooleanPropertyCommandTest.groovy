package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.parser.ResultParserException
import de.prob.prolog.output.IPrologTermOutput
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class CheckBooleanPropertyCommandTest extends Specification {
	def "CheckBooleanPropertyCommand is written correctly to Prolog"() {
		given:
		final cmd = new CheckBooleanPropertyCommand("A_DINGO_ATE_MY_BABY", "root")
		final IPrologTermOutput pto = Mock()

		when:
		cmd.writeCommand(pto)

		then:
		1 * pto.openTerm("state_property")
		1 * pto.printVariable(_ as String)
		1 * pto.printAtomOrNumber("root")
		1 * pto.printAtom("A_DINGO_ATE_MY_BABY")
	}

	def "valid results are processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm(s)
		}
		final cmd = new CheckBooleanPropertyCommand("BLAH_BLAH", "root")

		when:
		cmd.processResult(map)

		then:
		cmd.result == b

		where:
		s | b
		"true" | true
		"false" | false
	}

	def "an invalid result throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm("fff")
		}
		final cmd = new CheckBooleanPropertyCommand("BLAH_BLAH", "root")

		when:
		cmd.processResult(map)

		then:
		thrown(ResultParserException)
	}

	def "accessing the result before executing the command throws an exception"() {
		given:
		final cmd = new CheckBooleanPropertyCommand("BLAH_BLAH", "root")

		when:
		cmd.result

		then:
		thrown(IllegalStateException)
	}
}
