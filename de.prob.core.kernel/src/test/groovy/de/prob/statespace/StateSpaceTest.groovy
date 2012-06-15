package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*

import java.util.Random

import spock.lang.Specification
import de.prob.animator.IAnimator
import de.prob.exception.ProBError

class StateSpaceTest extends Specification {


	def StateSpace s

	def setup() {

		def mock = mock(IAnimator.class)

		doThrow(new ProBError("XXX")).when(mock).execute(any(Object.class));

		s = new StateSpace(mock, new DirectedMultigraphProvider(), new Random(), new History(), new StateSpaceInfo())

		s.addVertex(new StateId("1"))
		s.explored.add("1")

		addVertices([
			"root",
			"2",
			"3",
			"4",
			"5",
			"6"
		],s)
		s.addEdge(new StateId("root"),new StateId("2"),new OperationId("b"))
		s.addEdge(new StateId("2"),new StateId("3"),new OperationId("c"))
		s.addEdge(new StateId("3"),new StateId("4"),new OperationId("d"))
		s.addEdge(new StateId("3"),new StateId("5"),new OperationId("e"))

		s.history.add("2", "b")
		s.history.add("3", "c")


		s.explored.add("2")
		s.explored.add("3")
		s.explored.add("4")
		s.explored.add("5")

		s.addEdge( new StateId("4"), new StateId("6"),new OperationId("f"))
	}

	def addVertices(List<String> ids, StateSpace s) {
		for (String id : ids) {
			s.addVertex(new StateId(id))
		}
	}

	def "history current equals 1"() {
		expect:
		s.history.current == 1
	}

	def "The node is a deadlock"() {
		expect:
		s.isDeadlock("1") == true
	}

	def "The node is explored"() {
		expect:
		s.isExplored("1") == true
	}

	def "The stateid is unknown"() {
		when:
		s.isExplored("7")

		then:
		thrown IllegalArgumentException
	}

	def "The state is not explored"() {
		when:
		s.addVertex(new StateId("7"))

		then:
		s.isExplored("7") == false
	}

	def "The node is not a deadlock"() {
		def op = new Operation("a", "Blah", null);
		s.addEdge(new StateId("1"), new StateId("2"),new OperationId("blaOp"))

		expect:
		s.isDeadlock("1") == false
	}

	def "current state is 3"() {
		expect:
		s.getCurrentState() == "3"
	}

	def "previous state is 2"(){
		when:
		s.back()

		then:
		s.getCurrentState() == "2"
	}

	def "forward state after moving backwards is 3"() {
		when:
		s.back()
		s.forward()

		then:
		s.history.current == 1;
		s.getCurrentState() == "3"
	}

	def "user can step to node 4"() {
		when:
		s.step("d")

		then:
		s.getCurrentState() == "4"
		s.history.getCurrentTransition() == "d"
	}

	def "user can step to node 5"() {
		when:
		s.step("e")

		then:
		s.getCurrentState() == "5"
		s.history.getCurrentTransition() == "e"
	}

	def "c is the previous transition"() {
		expect:
		s.history.isLastTransition("c") == true
	}

	def "user cannot step to node root"() {
		when:
		s.step("b")

		then:
		thrown IllegalArgumentException
	}

	def "when user steps back, user can step forward"() {
		when:
		s.back()
		s.step("c")

		then:
		s.getCurrentState() == "3"
		s.history.getCurrentTransition() == "c"
	}

	def "test thrown exception for unexplored node 6"() {
		when:
		s.step("d")
		s.step("f")

		then:
		thrown ProBError 
	}

	def "test simple goToState"() {
		when:
		s.step("e")
		s.goToState("2")

		then:
		s.getCurrentState() == "2"
		s.history.history.size() == 4;
	}

	def "test multiple goToState"() {
		when:
		s.goToState("2")
		s.goToState("4")
		s.goToState("5")

		then:
		s.getCurrentState() == "5"
	}

	def "navigation with multiple goToState calls"() {
		when:
		s.goToState("2")
		s.goToState("4")
		s.goToState("5")
		s.back()

		then:
		// is this the intended behaviour?
		s.getCurrentState() == "3"
		s.history.isLastTransition(null) == false
		s.history.isLastTransition("c") == true
		s.history.isNextTransition(null) == true
	}

	def "navigation with multiple goToState calls 2"() {
		when:
		s.goToState("2")
		s.goToState("4")
		s.goToState("5")
		s.back()
		s.forward()

		then:
		s.history.history.size() == 3;
		s.getCurrentState() == "5"
		s.history.isLastTransition(null) == true
		s.history.isNextTransition("any") == false
	}

	def "null is neither the last or next transition if we are at the end"() {
		expect:
		s.history.isLastTransition(null) == false;
		s.history.isNextTransition(null) == false;
		s.history.history.size() == 2;
	}

	def "navigation with single goToState call"() {
		when:
		s.goToState("2")
		s.step("c")
		s.back()
		s.back()
		s.forward()

		then:
		s.history.history.size() == 4;
		s.getCurrentState() == "2"
		s.history.isLastTransition(null) == true
		s.history.isNextTransition("c") == true
	}

	def randomAnimSetup() {
		def animmock = mock(IAnimator.class)
		doThrow(new IllegalStateException()).when(animmock).execute(any(Object.class));

		def r = mock(Random.class)
		// take path: b, c, e, i, j, k
		when(r.nextInt(anyInt())).thenReturn(0, 0, 1, 2, 0, 0);


		s = new StateSpace(animmock, new DirectedMultigraphProvider(), r,new History(),new StateSpaceInfo())

		addVertices([
			"root",
			"2",
			"3",
			"4",
			"5",
			"6",
			"7",
			"8",
			"9",
			"10",
			"11"
		],s)

		s.addEdge(new StateId("root"),new StateId("2"),new OperationId("b"))
		s.addEdge(new StateId("2"),new StateId("3"),new OperationId("c"))
		s.addEdge(new StateId("3"),new StateId("4"),new OperationId("d"))
		s.addEdge(new StateId("3"),new StateId("4"),new OperationId("d"))
		s.addEdge(new StateId("3"),new StateId("5"),new OperationId("e"))
		s.addEdge(new StateId("4"),new StateId("6"),new OperationId("f"))
		s.addEdge(new StateId("5"),new StateId("7"),new OperationId("g"))
		s.addEdge(new StateId("5"),new StateId("8"),new OperationId("h"))
		s.addEdge(new StateId("5"),new StateId("9"),new OperationId("i"))
		s.addEdge(new StateId("9"),new StateId("10"),new OperationId("j"))
		s.addEdge(new StateId("10"),new StateId("11"),new OperationId("k"))

		s.explored.add("root")
		s.explored.add("2")
		s.explored.add("3")
		s.explored.add("4")
		s.explored.add("5")
		s.explored.add("6")
		s.explored.add("7")
		s.explored.add("8")
		s.explored.add("9")
		s.explored.add("10")
		s.explored.add("11")
	}

	def "testing random animation method"() {
		setup:
		randomAnimSetup()
		s.info.addInvOk("root", true)
		s.info.addInvOk("1", true)
		s.info.addInvOk("2", true)
		s.info.addInvOk("3", true)
		s.info.addInvOk("4", true)
		s.info.addInvOk("5", true)
		s.info.addInvOk("6", true)
		s.info.addInvOk("7", true)
		s.info.addInvOk("8", true)
		s.info.addInvOk("9", true)
		s.info.addInvOk("10", true)
		s.info.addInvOk("11", true)

		when:
		s.randomAnim(6);

		then:
		s.history.history.size() == 6;
		s.history.history.get(0).getOp() == "b"
		s.history.history.get(1).getOp() == "c"
		s.history.history.get(2).getOp() == "e"
		s.history.history.get(3).getOp() == "i"
		s.history.history.get(4).getOp() == "j"
		s.history.history.get(5).getOp() == "k"
	}

	def "testing random animation method with invariant violation"() {
		setup:
		randomAnimSetup()
		s.info.addInvOk("root", true)
		s.info.addInvOk("1", true)
		s.info.addInvOk("2", true)
		s.info.addInvOk("3", false)
		s.info.addInvOk("4", true)
		s.info.addInvOk("5", true)
		s.info.addInvOk("6", true)
		s.info.addInvOk("7", true)
		s.info.addInvOk("8", true)
		s.info.addInvOk("9", true)
		s.info.addInvOk("10", true)
		s.info.addInvOk("11", true)

		when:
		s.randomAnim(6);

		then:
		s.history.history.size() == 2;
		s.history.history.get(0).getOp() == "b"
		s.history.history.get(1).getOp() == "c"
	}

	def "testing random animation method with deadlocked state 3"() {
		setup:
		def animmock = mock(IAnimator.class)
		doThrow(new IllegalStateException()).when(animmock).execute(any(Object.class));

		def r = mock(Random.class)
		// take path: b, c, deadlock.
		when(r.nextInt(anyInt())).thenReturn(0, 0, 0, 0, 0, 0);


		s = new StateSpace(animmock, new DirectedMultigraphProvider(), r,new History(),new StateSpaceInfo())

		addVertices(["root", "2", "3"],s)

		s.addEdge(new StateId("root"),new StateId("2"),new OperationId("b"))
		s.addEdge(new StateId("2"),new StateId("3"),new OperationId("c"))

		s.explored.add("root")
		s.explored.add("2")
		s.explored.add("3")

		s.info.addInvOk("root", true)
		s.info.addInvOk("1", true)
		s.info.addInvOk("2", true)
		s.info.addInvOk("3", true)

		when:
		s.randomAnim(6);

		then:
		s.history.history.size() == 2;
		s.history.history.get(0).getOp() == "b"
		s.history.history.get(1).getOp() == "c"
	}

	def "testing multiple steps of looping edge"() {
		setup:
		s.addVertex(new StateId("3"))
		s.addEdge(new StateId("3"),new StateId("3"),new OperationId("loop"))

		expect:
		s.history.isLastTransition("c") == true
		s.history.history.size() == 2
		s.history.current == 1

		when:
		s.step("loop")
		s.step("loop")
		s.step("loop")
		s.step("loop")

		then:
		s.history.isLastTransition("loop") == true
		s.history.history.size() == 6
		s.history.current == 5
	}

	def "testing if canGoBack works"() {
		when:
		s.back()
		s.back()
		s.back()
		s.back()

		then:
		s.getCurrentState() == "root"
		s.history.current == -1
	}

	def "testing if canGoForward works"() {
		expect:
		s.getCurrentState() == "3"
		s.history.current == 1

		when:
		s.forward();

		then:
		s.getCurrentState() == "3"
		s.history.current == 1
	}

	def "testing if step works for integer opId"() {
		expect:
		s.getCurrentState() == "3"

		when:
		s.addVertex(new StateId("10"))
		s.addEdge(new StateId("3"),new StateId("10"),new OperationId("3"))
		s.explored.add("10")
		s.step(3)

		then:
		s.getCurrentState() == "10"
	}

	def "test register animation listener"() {
		when:
		def l = mock(IAnimationListener.class)
		s.registerAnimationListener(l)

		then:
		s.animationListeners.contains(l)
	}

	def "test register StateSpaceChangeListener"() {
		when:
		def l = mock(IStateSpaceChangeListener.class)
		s.registerStateSpaceListener(l)

		then:
		s.stateSpaceListeners.contains(l)
	}
}
