package de.prob.statespace

import de.prob.Main
import de.prob.scripting.ClassicalBFactory

import spock.lang.Specification

class TraceAnimationTest extends Specification {
	private static StateSpace s
	private Trace t

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.extract(path).load([:])
	}

	def cleanupSpec() {
		s.kill()
	}

	def setup() {
		t = new Trace(s)
	}

	def "if we specify that states should not be automatically explored, the destination state remains unexplored"() {
		given:
		t.exploreStateByDefault = false

		when:
		final t2 = t.add("0")

		then:
		!t2.currentTransition.destination.explored

		cleanup:
		t.exploreStateByDefault = true
	}

	def "you can add a transition via its integer id"() {
		when:
		final t2 = t.add(0)

		then:
		t2.currentTransition.name == "\$initialise_machine"
	}

	def "the empty trace has a size of 0"(){
		when:
		final t = new Trace(s)

		then:
		t.size() == 0
	}

	def "a trace containing three steps has a size of 3"(){
		when:
		final t = new Trace(s).anyEvent().anyEvent().anyEvent()

		then:
		t.size() == 3
	}

	def "you can add a transition via its string id"() {
		when:
		final t2 = t.add("0")

		then:
		t2.currentTransition.name == "\$initialise_machine"
	}

	def "you can add a transition via its Transition object"() {
		when:
		final t2 = t.add(t.currentState.outTransitions.find {it.id == "0"})

		then:
		t2.currentTransition.name == "\$initialise_machine"
	}

	def "you can add a transition via its string id unless that id doesn\'t exist yet"() {
		when:
		t.add("5")

		then:
		thrown(IllegalArgumentException)
	}

	def "you cannot add a Transition object that is null"() {
		when:
		t.add(null)

		then:
		final GroovyRuntimeException e = thrown()
		e.message.startsWith("Ambiguous method overloading");
	}

	def "you can add a transition with names and parameters"() {
		when:
		final t2 = t.$initialise_machine().addTransitionWith("new", ["PID1"])

		then:
		t2.currentTransition.name == "new"
		t2.currentTransition.parameterValues == ["PID1"]
	}

	def "you can add a transition with names and parameters unless it doesn't exist"() {
		when:
		t.$initialise_machine().addTransitionWith("new", ["PID7"])

		then:
		thrown(IllegalArgumentException)
	}

	def "by default, the destination state of the transition is explored"() {
		when:
		final t2 = t.add("0")

		then:
		t2.currentTransition.destination.explored
	}

	def "it is possible to move backward in a Trace"() {
		when:
		final t2 = t.$initialise_machine()
		final t3 = t2.new("pp = PID1")
		final t4 = t3.back()

		then:
		t2.canGoBack()
		t2.current == t4.current
		t3.current != t4.current
		t3.current.previous == t4.current
	}

	def "it is possible to move backward in a Trace unless you cannot go back"() {
		when:
		final t2 = t.back()

		then:
		!t.canGoBack()
		t2 == t
	}

	def "it is possible to move forward in a Trace"() {
		when:
		final t2 = t.$initialise_machine().new("pp = PID1").new("pp = PID2")
		final t3 = t2.back().back()
		final t4 = t3.forward()
		final t5 = t4.forward()

		then:
		t3.canGoForward()
		t4.canGoForward()
		!t5.canGoForward()
		t2.current == t5.current
		t5.current.previous == t4.current
		t4.current.previous == t3.current
	}

	def "it is possible to move forward unless you cannot move forward"() {
		expect:
		!t.canGoForward()
		t == t.forward()
	}

	def "if you are at the end of a trace, new transitions are simply appended to the existing list"() {
		when:
		final t2 = t.$initialise_machine().new("pp=PID1")
		final t3 = t2.new("pp=PID2")

		then:
		t2.transitionList.size() == 2
		t3.transitionList.size() == 3
	}

	def "if you are in the middle of the list, a new transition will branch the list"() {
		when:
		final t2 = t.$initialise_machine().new("pp=PID1")
		final t3 = t2.back().new("pp=PID2").new("pp=PID3")

		then:
		t2.transitionList.size() == 2
		t3.transitionList.size() == 3
		!t3.transitionList.is(t2.transitionList)
	}

	def "there is a correct toString method for Trace"() {
		expect:
		t.toString() != null
	}

	def "there is a correct toString method for Traces with elements"() {
		expect:
		t.$initialise_machine().toString() != null
	}

	def "random animation with a minus number returns the original trace"() {
		expect:
		t.randomAnimation(-1) == t
	}

	def "random animation with 0 returns original trace"() {
		expect:
		t.randomAnimation(0) == t
	}

	def "random animation works for positive numbers"() {
		when:
		final t2 = t.randomAnimation(5)

		then:
		t2.transitionList.size() == 5
	}

	def "branching works for random animation as well"() {
		when:
		final t2 = t.$initialise_machine().new("pp=PID1")
		final t3 = t2.back().randomAnimation(4)

		then:
		!t2.transitionList.is(t3.transitionList)
		t3.transitionList.size() == 5
	}

	def "can execute transition by invoking method"() {
		when:
		final t2 = t.$initialise_machine()
		final t3 = t.invokeMethod("\$initialise_machine", [])

		then:
		t2.currentTransition.name == "\$initialise_machine"
		t3.currentTransition.name == "\$initialise_machine"
	}

	def "cannot execute transition by invoking method if it doesn't exist"() {
		when:
		t.blah()

		then:
		thrown(IllegalArgumentException)
	}

	def "can execute transition with execute method"() {
		when:
		final t2 = t.$initialise_machine().execute("new", ["pp=PID1"])

		then:
		t2.currentTransition.name == "new"
		t2.currentTransition.parameterValues == ["PID1"]
	}

	def "cannot execute transition with execute method if it doesn't exist"() {
		when:
		t.$initialise_machine().execute("new", ["pp=PID7"])

		then:
		thrown(IllegalArgumentException)
	}

	def "can execute transition with execute varargs method"() {
		when:
		final t2 = t.$initialise_machine().execute("new", "pp=PID1")

		then:
		t2.currentTransition.name == "new"
		t2.currentTransition.parameterValues == ["PID1"]
	}

	def "cannot execute transition with execute varargs method if it doesn't exist"() {
		when:
		t.$initialise_machine().execute("new", "pp=PID7")

		then:
		thrown(IllegalArgumentException)
	}

	def "can test if a transition can be executed"() {
		expect:
		t.$initialise_machine().canExecuteEvent("new", ["pp=PID1"])
	}

	def "can test if a transition cannot be executed"() {
		expect:
		!t.$initialise_machine().canExecuteEvent("new", ["pp=PID7"])
	}

	def "can test if a transition can be executed (varargs)"() {
		expect:
		t.$initialise_machine().canExecuteEvent("new", "pp=PID1")
	}

	def "can test if a transition cannot be executed (varargs)"() {
		expect:
		!t.$initialise_machine().canExecuteEvent("new", "pp=PID7")
	}

	def "can execute transitions via the anyOperation method"() {
		when:
		final t2 = t.anyOperation()
		final t3 = t.anyOperation().anyOperation("new")
		final t4 = t.anyOperation().anyOperation(["new"])
		final t5 = t.anyOperation("blah") // will return original trace

		then:
		t2.currentTransition.name == "\$initialise_machine"
		t3.currentTransition.name == "new"
		t4.currentTransition.name == "new"
		t5 == t
	}

	def "can execute transitions via the anyEvent method"() {
		when:
		final t2 = t.anyEvent()
		final t3 = t.anyEvent().anyEvent("new")
		final t4 = t.anyEvent().anyEvent(["new"])
		final t5 = t.anyEvent("blah") // will return original trace

		then:
		t2.currentTransition.name == "\$initialise_machine"
		t3.currentTransition.name == "new"
		t4.currentTransition.name == "new"
		t5 == t
	}

	def "can use the static method to change a list of transitions into a Trace"() {
		when:
		final transitions = []
		def state = s.root
		for (i in 0..9) {
			final transition = state.outTransitions.first()
			transitions << transition
			state = transition.destination
		}
		final t = Trace.getTraceFromTransitions(s, transitions)
		final t2 = Trace.getTraceFromTransitions(s, transitions[3..5])
		final t3 = Trace.getTraceFromTransitions(s, []) // empty trace

		then:
		t.transitionList.first().source == s.root

		t2.transitionList.size() == 3
		t2.transitionList[0] == transitions[3]
		t2.currentState == transitions[5].destination

		t3.currentState == s.root
	}

	def "can add a list of transitions into a Trace"() {
		when:
		final transitions = []
		def state = s.root
		for (i in 0..9) {
			final transition = state.outTransitions.first()
			transitions << transition
			state = transition.destination
		}
		final t1 = t.addTransitions(transitions)
		final t2 = t.addTransitions([]) // empty

		then:
		t1.transitionList.first().source == s.root
		t1.transitionList.size() == 10

		t2 == t
	}
}
