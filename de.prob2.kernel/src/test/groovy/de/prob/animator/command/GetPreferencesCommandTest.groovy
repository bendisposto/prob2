package de.prob.animator.command

import de.prob.parser.ISimplifiedROMap
import de.prob.parser.ResultParserException
import de.prob.prolog.output.StructuredPrologOutput
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.ListPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class GetPreferencesCommandTest extends Specification {
	def "the list of preferences is null before processing results"() {
		given:
		final gpc = new GetDefaultPreferencesCommand()

		expect:
		gpc.preferences == null
	}

	def "GetDefaultPreferencesCommand is written correctly to Prolog"() {
		given:
		final pto = new StructuredPrologOutput()
		final command = new GetDefaultPreferencesCommand()

		when:
		command.writeCommand(pto)
		pto.fullstop()
		pto.flush()

		then:
		final term = pto.sentences.iterator().next()
		term != null
		term instanceof CompoundPrologTerm
		term.functor == "list_all_eclipse_preferences"
		term.arity == 1
		term.getArgument(1).variable
	}

	def "a valid result is processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("Prefs") >> new ListPrologTerm(
				new CompoundPrologTerm("preference",
					new CompoundPrologTerm("tinker"),
					new CompoundPrologTerm("tailor"),
					new CompoundPrologTerm("soldier"),
					new CompoundPrologTerm("sailor"),
					new CompoundPrologTerm("foo"),
				),
				new CompoundPrologTerm("preference",
					new CompoundPrologTerm("richman"),
					new CompoundPrologTerm("poorman"),
					new CompoundPrologTerm("beggarman"),
					new CompoundPrologTerm("thief"),
					new CompoundPrologTerm("bar"),
				),
			)
		}
		final command = new GetDefaultPreferencesCommand()

		when:
		command.processResult(map)

		then:
		command.preferences.size() == 2

		final pref1 = command.preferences[0]
		pref1.name == "tinker"
		pref1.type as String == "tailor"
		pref1.description == "soldier"
		pref1.category == "sailor"
		pref1.defaultValue == "foo"

		final pref2 = command.preferences[1]
		pref2.name == "richman"
		pref2.type as String == "poorman"
		pref2.description == "beggarman"
		pref2.category == "thief"
		pref2.defaultValue == "bar"
	}

	def "a result list containing an invalid term throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get(_ as String) >> new ListPrologTerm(new CompoundPrologTerm("blah blah blah"))
		}
		final command = new GetDefaultPreferencesCommand()

		when:
		command.processResult(map)

		then:
		thrown(ResultParserException)
	}

	def "an invalid result term throws an exception"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get(_ as String) >> new CompoundPrologTerm("blah blah blah")
		}
		final command = new GetDefaultPreferencesCommand()

		when:
		command.processResult(map)

		then:
		thrown(ResultParserException)
	}
}
