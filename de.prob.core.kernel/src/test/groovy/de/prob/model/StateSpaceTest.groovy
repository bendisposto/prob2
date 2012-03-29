package de.prob.model


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Ignore
import spock.lang.Specification
import de.prob.ProBException
import de.prob.animator.IAnimator
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType

class StateSpaceTest extends Specification {


	def StateSpace s

	def setup() {

		def mock = mock(IAnimator.class)

		doThrow(new ProBException()).when(mock).execute(any(Object.class));

		s = new StateSpace(mock, new DirectedSparseMultigraph<String, String>())

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

	@Ignore
	def "The node is not a deadlock"() {
		def op = new Operation("a", "Blah", null);
		s.addEdge(op, "1", "2", EdgeType.DIRECTED);

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
	
	def "test simple goToState"()
	{
		when:
		s.step("e")
		s.goToState("2")
		
		then:	
		s.getCurrentState() == "2"
	}
		
	def "test multiple goToState"()
	{
		when:
		s.goToState("2")
		s.goToState("4")
		s.goToState("5")
		
		then:
		s.getCurrentState() == "5"
	}
	
	def "navigation with multiple goToState calls"()
	{
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
	
	def "navigation with multiple goToState calls 2"()
	{
		when:
		s.goToState("2")
		s.goToState("4")
		s.goToState("5")
		s.back()
		s.forward()
		
		then:
		s.getCurrentState() == "5"
		s.history.isLastTransition(null) == true
		s.history.isNextTransition("any") == false
	}
}
