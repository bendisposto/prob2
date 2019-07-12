package de.prob.animator.command


import de.prob.parser.ISimplifiedROMap
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.PrologTerm

import spock.lang.Specification 

class SpecializedBooleanCheckersCommandTest extends Specification {
	def "CheckInitialisationStatusCommand results are processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm(s)
		}
		final command = new CheckInitialisationStatusCommand("root")

		when:
		command.processResult(map)

		then:
		command.initialized == b

		where:
		s | b
		"true" | true
		"false" | false
	}

	def "CheckInvariantStatusCommand results are processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm(s)
		}
		final command = new CheckInvariantStatusCommand("root")

		when:
		command.processResult(map)

		then:
		command.invariantViolated == b

		where:
		s | b
		"true" | true
		"false" | false
	}

	def "CheckMaxOperationReachedStatusCommand results are processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm(s)
		}
		final command = new CheckMaxOperationReachedStatusCommand("root")

		when:
		command.processResult(map)

		then:
		command.maxOperationReached() == b

		where:
		s | b
		"true" | true
		"false" | false
	}

	def "CheckTimeoutStatusCommand results are processed correctly"() {
		given:
		final ISimplifiedROMap<String, PrologTerm> map = Mock() {
			get("PropResult") >> new CompoundPrologTerm(s)
		}
		final command = new CheckTimeoutStatusCommand("root")

		when:
		command.processResult(map)

		then:
		command.timeout == b

		where:
		s | b
		"true" | true
		"false" | false
	}
}
