package de.prob.statespace

import static org.mockito.Mockito.*
import spock.lang.Specification

import com.github.krukow.clj_lang.PersistentVector

class AnimationSelectorTest extends Specification {

	class MyListener implements IAnimationChangeListener {
		def int count;

		def MyListener() {
			count = 0;
		}

		@Override
		public void traceChange(Trace trace, boolean currentAnimationChanged) {
			count++;
		}

		@Override
		public void animatorStatus(boolean busy) {
		}
	}

	def Trace trace
	def AnimationSelector selector
	def listener

	def setup() {
		def StateSpace ss = mock(StateSpace.class);
		when(ss.isBusy()).thenReturn(false);
		trace = new Trace(ss,null,PersistentVector.emptyVector(),UUID.randomUUID());
		selector = new AnimationSelector();
		listener = new MyListener()
		selector.registerAnimationChangeListener(listener)
	}

	def "It is possible to register a listener"() {
		expect:
		selector.traceListeners.size() == 1
		selector.traceListeners.get(0).get() == listener
	}

	def "It is possible to notify the listener"() {
		when:
		selector.notifyAnimationChange(trace, true)

		then:
		listener.getCount() == 1
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
