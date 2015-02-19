package de.prob.statespace

import spock.lang.Specification
import de.prob.Main
import de.prob.model.representation.AbstractModel
import de.prob.scripting.ClassicalBFactory

class TraceAnimationTest extends Specification {

	static AbstractModel m
	Trace t

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		m = factory.load(path)
	}

	def setup() {
		t = new Trace(m)
	}

	def "if we specify that states should not be automatically explored, the destination state remains unexplored"() {
		when:
		Trace.exploreStateByDefault = false
		Trace t2 = t.add("0")

		then:
		!t2.getCurrentTransition().getDestination().isExplored()

		cleanup:
		Trace.exploreStateByDefault = true
	}

	def "you can add a transition via its integer id"() {
		when:
		Trace t2 = t.add(0)

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
	}

	def "you can add a transition via its string id"() {
		when:
		Trace t2 = t.add("0")

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
	}

	def "you can add a transition via its Transition object"() {
		when:
		Trace t2 = t.add(t.getCurrentState().getOutTransitions().find { it.getId() == "0"})

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
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
		def e = thrown(GroovyRuntimeException)
		e.getMessage().startsWith("Ambiguous method overloading");
	}

	def "you can add a transition with names and parameters"() {
		when:
		Trace t2 = t.$initialise_machine().addTransitionWith("new",["PID1"])

		then:
		t2.getCurrentTransition().getName() == "new"
		t2.getCurrentTransition().getParams() == ["PID1"]
	}

	def "you can add a transition with names and parameters unless it doesn't exist"() {
		when:
		Trace t2 = t.$initialise_machine().addTransitionWith("new",["PID7"])

		then:
		thrown(IllegalArgumentException)
	}

	def "by default, the destination state of the transition is explored"() {
		when:
		Trace t2 = t.add("0")

		then:
		t2.getCurrentTransition().getDestination().isExplored()
	}

	def "it is possible to move backward in a Trace"() {

		when:
		Trace t2 = t.$initialise_machine()
		Trace t3 = t2.new("pp = PID1")
		Trace t4 = t3.back()

		then:
		t2.canGoBack()
		t2.current == t4.current
		t3.current != t4.current
		t3.current.previous == t4.current
	}

	def "it is possible to move backward in a Trace unless you cannot go back"() {
		when:
		Trace t2 = t.back()

		then:
		!t.canGoBack()
		t2 == t
	}

	def "it is possible to move forward in a Trace"() {

		when:
		Trace t2 = t.$initialise_machine().new("pp = PID1").new("pp = PID2")
		Trace t3 = t2.back().back()
		Trace t4 = t3.forward()
		Trace t5 = t4.forward()

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
		Trace t2 = t.$initialise_machine().new("pp=PID1")
		def t2list = t2.transitionList
		def t2listSize = t2list.size()
		Trace t3 = t2.new("pp=PID2")
		def t3list = t3.transitionList

		then:
		t2listSize == 2
		t2list.is(t3list)
		t3list.size() == 3
	}

	def "if you are in the middle of the list, a new transition will branch the list"() {
		when:
		Trace t2 = t.$initialise_machine().new("pp=PID1")
		def t2list = t2.transitionList
		Trace t3 = t2.back().new("pp=PID2").new("pp=PID3")
		def t3list = t3.transitionList

		then:
		t2list.size() == 2
		t3list.size() == 3
		!t3list.is(t2list)
	}

	def "there is a correct toString method for Trace"() {
		expect: t.toString() != null
	}

	def "there is a correct toString method for Traces with elements"() {
		expect: t.$initialise_machine().toString() != null
	}

	def "random animation with a minus number returns the original trace"() {
		expect: t.randomAnimation(-1) == t
	}

	def "random animation with 0 returns original trace"() {
		expect: t.randomAnimation(0) == t
	}

	def "random animation works for positive numbers"() {
		when:
		Trace t2 = t.randomAnimation(5)

		then:
		t2.transitionList.size() == 5
	}

	def "branching works for random animation as well"() {
		when:
		Trace t2 = t.$initialise_machine().new("pp=PID1")
		Trace t3 = t2.back().randomAnimation(4)

		then:
		!t2.transitionList.is(t3.transitionList)
		t3.transitionList.size() == 5
	}

	def "can execute transition by invoking method"() {
		when:
		Trace t2 = t.$initialise_machine()
		Trace t3 = t.invokeMethod("\$initialise_machine", [])

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
		t3.getCurrentTransition().getName() == "\$initialise_machine"
	}

	def "cannot execute transition by invoking method if it doesn't exist"() {
		when:
		Trace t2 = t.blah()

		then:
		thrown(IllegalArgumentException)
	}

	def "can execute transition with execute method"() {
		when:
		Trace t2 = t.$initialise_machine().execute("new",["pp=PID1"])

		then:
		t2.getCurrentTransition().getName() == "new"
		t2.getCurrentTransition().getParams() == ["PID1"]
	}

	def "cannot execute transition with execute method if it doesn't exist"() {
		when:
		Trace t2 = t.$initialise_machine().execute("new",["pp=PID7"])

		then:
		thrown(IllegalArgumentException)
	}

	def "can execute transition with execute varargs method"() {
		when:
		Trace t2 = t.$initialise_machine().execute("new","pp=PID1")

		then:
		t2.getCurrentTransition().getName() == "new"
		t2.getCurrentTransition().getParams() == ["PID1"]
	}

	def "cannot execute transition with execute varargs method if it doesn't exist"() {
		when:
		Trace t2 = t.$initialise_machine().execute("new","pp=PID7")

		then:
		thrown(IllegalArgumentException)
	}

	def "can test if a transition can be executed"() {
		expect: t.$initialise_machine().canExecuteEvent("new",["pp=PID1"])
	}

	def "can test if a transition cannot be executed"() {
		expect: !t.$initialise_machine().canExecuteEvent("new",["pp=PID7"])
	}

	def "can test if a transition can be executed (varargs)"() {
		expect: t.$initialise_machine().canExecuteEvent("new","pp=PID1")
	}

	def "can test if a transition cannot be executed (varargs)"() {
		expect: !t.$initialise_machine().canExecuteEvent("new","pp=PID7")
	}

	def "can execute transitions via the anyOperation method"() {
		when:
		Trace t2 = t.anyOperation()
		Trace t3 = t.anyOperation().anyOperation("new")
		Trace t4 = t.anyOperation().anyOperation(["new"])
		Trace t5 = t.anyOperation("blah") // will return original trace

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
		t3.getCurrentTransition().getName() == "new"
		t4.getCurrentTransition().getName() == "new"
		t5 == t
	}

	def "can execute transitions via the anyEvent method"() {
		when:
		Trace t2 = t.anyEvent()
		Trace t3 = t.anyEvent().anyEvent("new")
		Trace t4 = t.anyEvent().anyEvent(["new"])
		Trace t5 = t.anyEvent("blah") // will return original trace

		then:
		t2.getCurrentTransition().getName() == "\$initialise_machine"
		t3.getCurrentTransition().getName() == "new"
		t4.getCurrentTransition().getName() == "new"
		t5 == t
	}

	def "can use the static method to change a list of transitions into a Trace"() {
		when:
		StateSpace s = m as StateSpace
		def transitions = []
		def state = s.getRoot()
		for (i in 0..9) {
			def t = state.getOutTransitions().first()
			transitions << t
			state = t.getDestination()
		}
		Trace t = Trace.getTraceFromTransitions(s, transitions)
		Trace t2 = Trace.getTraceFromTransitions(s, transitions[3..5])
		Trace t3 = Trace.getTraceFromTransitions(s, []) // empty trace

		then:
		t.getTransitionList().first().getSource() == s.getRoot()

		t2.getTransitionList().size() == 3
		t2.getTransitionList()[0] == transitions[3]
		t2.getCurrentState() == transitions[5].getDestination()

		t3.getCurrentState() == s.getRoot()
	}

	def "can add a list of transitions into a Trace"() {
		when:
		StateSpace s = m as StateSpace
		def transitions = []
		def state = s.getRoot()
		for (i in 0..9) {
			def t = state.getOutTransitions().first()
			transitions << t
			state = t.getDestination()
		}
		Trace t1 = t.addTransitions(transitions)
		Trace t2 = t.addTransitions([]) // empty

		then:
		t1.getTransitionList().first().getSource() == s.getRoot()
		t.getTransitionList().size() == 10

		t2 == t

	}
}
