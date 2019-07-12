package de.prob.statespace

import com.github.krukow.clj_lang.PersistentVector

import spock.lang.Specification 

class AnimationSelectorTest extends Specification {
	private Trace trace
	private AnimationSelector selector
	private IAnimationChangeListener listener

	def setup() {
		final StateSpace ss = Mock() {
			isBusy() >> false
		}
		trace = new Trace(ss, null, PersistentVector.emptyVector(), UUID.randomUUID())
		selector = new AnimationSelector()
		listener = Mock()
		selector.registerAnimationChangeListener(listener)
	}

	def "It is possible to register a listener"() {
		expect:
		selector.traceListeners.size() == 1
		selector.traceListeners[0] == listener
	}

	def "It is possible to notify the listener"() {
		when:
		selector.notifyAnimationChange(trace, true)

		then:
		1 * listener.traceChange(trace, true)
	}

	def "It is possible to add a new Trace"() {
		when:
		selector.addNewAnimation(trace)

		then:
		trace in selector.traces
	}

	def "It is possible to change the current Trace"() {
		when:
		selector.changeCurrentAnimation(trace)

		then:
		selector.currentTrace == trace
	}
}
