package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.output.StructuredPrologOutput
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class GetErrorsCommandTest extends Specification {
	def "GetErrorsCommand is written correctly to Prolog"() {
		given:
		final pto = new StructuredPrologOutput()
		final command = new GetErrorsCommand()

		when:
		command.writeCommand(pto)
		pto.fullstop()
		pto.flush()

		then:
		final term = pto.sentences[0]
		term != null
		term instanceof CompoundPrologTerm
		term.functor == "get_error_messages"
		term.arity == 2
		term.getArgument(1).variable
	}

	def "a valid result is processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get(_ as String) >> new ListPrologTerm(new CompoundPrologTerm("foobar"))
		}
		final command = new GetErrorsCommand()

		when:
		command.processResult(map)

		then:
		command.errors[0] == "foobar"
	}
}
