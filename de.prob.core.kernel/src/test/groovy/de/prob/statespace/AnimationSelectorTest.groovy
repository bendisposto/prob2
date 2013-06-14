package de.prob.statespace

import static org.mockito.Mockito.*
import spock.lang.Specification

class AnimationSelectorTest extends Specification {

	def trace
	def AnimationSelector selector
	def listener

	def setup() {
		trace = mock(Trace.class);
		selector = new AnimationSelector();
		listener = new IAnimationChangeListener() {
					def count = 0;
					void traceChange(Trace arg0) {
						count++;
					};
				}
		selector.registerAnimationChangeListener(listener)
	}

	def "It is possible to register a listener"() {
		expect:
		selector.traceListeners.size() == 1
		selector.traceListeners.get(0).get() == listener
	}

	def "It is possible to notify the listener"() {
		when:
		selector.notifyAnimationChange(null)

		then:
		listener.count == 1
	}

	def "It is possible to add a new History"() {
		when:
		selector.addNewAnimation(trace)

		then:
		selector.traces.contains(trace)
	}

	def "It is possible to change the current history"() {
		when:
		selector.changeCurrentAnimation(trace)

		then:
		selector.currentTrace == trace
	}
}
