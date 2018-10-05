package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.parser.ResultParserException
import de.prob.prolog.output.StructuredPrologOutput
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class GetOperationsWithTimeoutTest extends Specification {
	def "GetOperationsWithTimeout is written correctly to Prolog"() {
		given:
		final pto = new StructuredPrologOutput()
		final command = new GetOperationsWithTimeout("state")

		when:
		command.writeCommand(pto)
		pto.fullstop()
		pto.flush()

		then:
		final term = pto.sentences[0]
		term != null
		term instanceof CompoundPrologTerm
		term.functor == "op_timeout_occurred"
		term.arity == 2
		term.getArgument(1).atom
		term.getArgument(2).variable
	}

	def "a valid result is processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("TO") >> new ListPrologTerm(
				new CompoundPrologTerm("foobar"),
				new CompoundPrologTerm("bliblablub"),
			)
		}
		final command = new GetOperationsWithTimeout("state")

		when:
		command.processResult(map)
		
		then:
		command.timeouts[0] == "foobar"
		command.timeouts[1] == "bliblablub"
	}

	def "an invalid result throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get(_ as String) >> new CompoundPrologTerm("bang!!!")
		}
		final command = new GetOperationsWithTimeout("state")

		when:
		command.processResult(map)

		then:
		thrown(ResultParserException)
	}
}
