package de.prob.statespace

import static org.mockito.Mockito.*
import spock.lang.Specification

class AnimationSelectorTest extends Specification {

	def history
	def selector
	def listener

	def setup() {
		history = mock(History.class);
		selector = new AnimationSelector();
		listener = new IHistoryChangeListener() {
					def count = 0;
					void historyChange(History arg0) {
						count++;
					};
				}
		selector.registerHistoryChangeListener(listener)
	}

	def "It is possible to register a listener"() {
		expect:
		selector.historyListeners.size() == 1
	}

	def "It is possible to notify the listener"() {
		when:
		selector.notifyHistoryChange(null)

		then:
		listener.count == 1
	}

	def "It is possible to add a new History"() {
		when:
		selector.addNewHistory(history)

		then:
		selector.histories.contains(history)
	}

	def "It is possible to change the current history"() {
		when:
		selector.changeCurrentHistory(history)

		then:
		selector.currentHistory == history
	}
}
