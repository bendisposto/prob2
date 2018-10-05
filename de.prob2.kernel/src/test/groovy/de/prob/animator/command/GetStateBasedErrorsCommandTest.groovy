package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.parser.ResultParserException
import de.prob.prolog.output.StructuredPrologOutput
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class GetStateBasedErrorsCommandTest extends Specification {
	def "GetStateBasedErrorsCommand is written correctly to Prolog"() {
		given:
		final pto = new StructuredPrologOutput()
		final command = new GetStateBasedErrorsCommand("42")

		when:
		command.writeCommand(pto)
		pto.fullstop()
		pto.flush()

		then:
		final term = pto.sentences[0]
		term != null
		term instanceof CompoundPrologTerm
		term.functor == "get_state_errors"
		term.arity == 2
		term.getArgument(1).number
		term.getArgument(2).variable
	}

	def "a nonempty result is processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("Errors") >> new ListPrologTerm(
				new CompoundPrologTerm("error",
					new CompoundPrologTerm("foo"),
					new CompoundPrologTerm("bar"),
					new CompoundPrologTerm("baz"),
				),
			)
		}
		final command = new GetStateBasedErrorsCommand("state")

		when:
		command.processResult(map)

		then:
		final se = command.result[0]
		se.event == "foo"
		se.shortDescription == "bar"
		se.longDescription == "baz"
	}

	def "an empty result is processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("Errors") >> new ListPrologTerm()
		}
		final command = new GetStateBasedErrorsCommand("state")

		when:
		command.processResult(map)

		then:
		command.result.empty
	}

	def "a result list containing an invalid term throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("Errors") >> new ListPrologTerm(new CompoundPrologTerm("foobar"))
		}
		final command = new GetStateBasedErrorsCommand("state")

		when:
		command.processResult(map)

		then:
		thrown(ResultParserException)
	}

	def "an invalid result term throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("Errors") >> new CompoundPrologTerm("foobar")
		}
		final command = new GetStateBasedErrorsCommand("state")

		when:
		command.processResult(map)

		then:
		thrown(ResultParserException)
	}
}
