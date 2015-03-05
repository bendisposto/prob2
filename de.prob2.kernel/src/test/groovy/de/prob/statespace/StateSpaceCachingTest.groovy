package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification

import com.google.common.util.concurrent.UncheckedExecutionException

import de.prob.Main
import de.prob.scripting.ClassicalBFactory


class StateSpaceCachingTest extends Specification {

	static StateSpace s

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.load(path) as StateSpace
	}

	def setup() {
		s.states.invalidateAll();
	}

	def "at the beginning, root is not in the state space"() {
		expect: s.states.getIfPresent("root") == null
	}

	def "after accessing root, it is present in cache"() {
		expect:
		s.getRoot() != null
		s.states.getIfPresent("root") != null
	}

	def "states that are discovered during exploration are automatically stored cache"() {
		when:
		s.getRoot().explore()

		then:
		s.states.getIfPresent("0") != null
		s.getState("0") != null
	}

	def "but if the state is gone from cache for some reason, it can be reretrieved from prolog"() {
		when:
		s.getRoot().explore()
		s.states.invalidate("0")

		then:
		s.states.getIfPresent("0") == null
		s.getState("0") != null
		s.states.getIfPresent("0") != null
	}

	def "if a state does not exist in the state space (on the prolog side) and illegal argument exception is thrown"() {
		when:
		s.getState("bum!");

		then:
		thrown(IllegalArgumentException)
	}

	def "states can also be accessed via integer values whereby root is -1"() {
		expect:
		s.getState(-1) == s.getRoot()
	}

	def "states can also be accessed via integer values if the states exist"() {
		when:
		s.getRoot().explore()

		then:
		s.getState(0) == s.getState("0")
	}

	def "states cannot be accessed via negative integer except -1 (root)"() {
		when:
		s.getState(-100)

		then:
		thrown(IllegalArgumentException)
	}

	def "states can be accessed via integer via the getAt method"() {
		when:
		s.getRoot().explore()

		then:
		s.getAt(0).getId() == "0"
		s.getAt(0) == s[0]
	}

	def "states that do not exist in the prolog kernel cannot be accessed via integer"() {
		when:
		s.getState(500) // we have not reached this during the exploration we have done so far in the tests

		then:
		thrown(IllegalArgumentException)
	}

	def "the cache is emptied when it gets too full"() {
		setup:
		Main.maxCacheSize = 5

		when:
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		StateSpace s = factory.load(path) as StateSpace
		Trace t = new Trace(s)
		def sizes = []
		for (i in 1..10) {
			t = t.anyEvent()
			sizes << s.states.size()
		}

		then:
		sizes.inject(true) { result, i -> result && (i <= 5) }

		cleanup:
		Main.maxCacheSize = 100
	}

	def "trying to get a key from the LoadingCache that doesn't exist results in an exception"() {
		when:
		def t = s.states.get("b")

		then:
		thrown(UncheckedExecutionException)
	}
}
