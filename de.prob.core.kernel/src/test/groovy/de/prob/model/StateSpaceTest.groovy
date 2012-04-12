package de.prob.model


import static org.junit.Assert.*
import static org.mockito.Mockito.*

import java.util.Random

import spock.lang.Ignore;
import spock.lang.Specification
import de.prob.ProBException
import de.prob.animator.IAnimator
import edu.uci.ics.jung.graph.DirectedSparseMultigraph

class StateSpaceTest extends Specification {


	def StateSpace s

	def setup() {

		def mock = mock(IAnimator.class)

		doThrow(new ProBException()).when(mock).execute(any(Object.class));

		s = new StateSpace(mock, new DirectedSparseMultigraph<String, String>(), new Random())

		s.addVertex("1")
		s.explored.add("1")

		s.addEdge("b","root","2")
		s.addEdge("c","2","3")
		s.addEdge("d","3","4")
		s.addEdge("e","3","5")

		s.history.add("2", "b")
		s.history.add("3", "c")

		s.explored.add("2")
		s.explored.add("3")
		s.explored.add("4")
		s.explored.add("5")

		s.addEdge("f", "4", "6")
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
		s.addVertex("7")

		then:
		s.isExplored("7") == false
	}

	def "The node is not a deadlock"() {
		def op = new Operation("a", "Blah", null);
		s.addEdge("blaOp", "1", "2")

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

	def "user can step to node 2"() {
		when:
		s.step("c")

		then:
		s.getCurrentState() == "2"
		s.history.getCurrentTransition() == "b"
	}

	def "user cannot step to node root"() {
		when:
		s.step("b")

		then:
		s.getCurrentState() == "3"
		s.history.getCurrentTransition() == "c"
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
		thrown ProBException
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

	def "testing random animation method"() {
		setup:
		def animmock = mock(IAnimator.class)
		doThrow(new ProBException()).when(animmock).execute(any(Object.class));

		def r = mock(Random.class)
		// take path: b, c, e, i, j, k
		when(r.nextInt(anyInt())).thenReturn(0, 0, 1, 2, 0, 0);


		s = new StateSpace(animmock, new DirectedSparseMultigraph<String, String>(), r)

		s.addEdge("b","root","2")
		s.addEdge("c","2","3")
		s.addEdge("d","3","4")
		s.addEdge("e","3","5")
		s.addEdge("f", "4", "6")
		s.addEdge("g", "5", "7")
		s.addEdge("h", "5", "8")
		s.addEdge("i", "5", "9")
		s.addEdge("j", "9", "10")
		s.addEdge("k", "10", "11")

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

		s.invariantOk.put("root", true)
		s.invariantOk.put("1", true)
		s.invariantOk.put("2", true)
		s.invariantOk.put("3", true)
		s.invariantOk.put("4", true)
		s.invariantOk.put("5", true)
		s.invariantOk.put("6", true)
		s.invariantOk.put("7", true)
		s.invariantOk.put("8", true)
		s.invariantOk.put("9", true)
		s.invariantOk.put("10", true)
		s.invariantOk.put("11", true)

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
		def animmock = mock(IAnimator.class)
		doThrow(new ProBException()).when(animmock).execute(any(Object.class));

		def r = mock(Random.class)
		// take path: b, c, e, i, j, k
		when(r.nextInt(anyInt())).thenReturn(0, 0, 1, 2, 0, 0);


		s = new StateSpace(animmock, new DirectedSparseMultigraph<String, String>(), r)

		s.addEdge("b","root","2")
		s.addEdge("c","2","3")
		s.addEdge("d","3","4")
		s.addEdge("e","3","5")
		s.addEdge("f", "4", "6")
		s.addEdge("g", "5", "7")
		s.addEdge("h", "5", "8")
		s.addEdge("i", "5", "9")
		s.addEdge("j", "9", "10")
		s.addEdge("k", "10", "11")

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

		s.invariantOk.put("root", true)
		s.invariantOk.put("1", true)
		s.invariantOk.put("2", true)
		s.invariantOk.put("3", false)
		s.invariantOk.put("4", true)
		s.invariantOk.put("5", true)
		s.invariantOk.put("6", true)
		s.invariantOk.put("7", true)
		s.invariantOk.put("8", true)
		s.invariantOk.put("9", true)
		s.invariantOk.put("10", true)
		s.invariantOk.put("11", true)

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
		doThrow(new ProBException()).when(animmock).execute(any(Object.class));

		def r = mock(Random.class)
		// take path: b, c, deadlock.
		when(r.nextInt(anyInt())).thenReturn(0, 0, 0, 0, 0, 0);


		s = new StateSpace(animmock, new DirectedSparseMultigraph<String, String>(), r)

		s.addEdge("b","root","2")
		s.addEdge("c","2","3")

		s.explored.add("root")
		s.explored.add("2")
		s.explored.add("3")

		s.invariantOk.put("root", true)
		s.invariantOk.put("1", true)
		s.invariantOk.put("2", true)
		s.invariantOk.put("3", true)

		when:
		s.randomAnim(6);

		then:
		s.history.history.size() == 2;
		s.history.history.get(0).getOp() == "b"
		s.history.history.get(1).getOp() == "c"
	}

	@Ignore
	def "testing multiple steps of looping edge"() {
		setup:
		s.addEdge("loop","3","3")

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
		s.history.history.size() == 3
		s.history.current == 2
	}
}
