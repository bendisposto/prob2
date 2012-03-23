package de.prob.model


import static org.junit.Assert.*
import spock.lang.Specification
import de.prob.model.StateSpace
import de.prob.animator.IAnimator
import edu.uci.ics.jung.graph.util.EdgeType;
import de.prob.model.Operation

class StateSpaceTest extends Specification {

	def StateSpace s

	def setup() {
		s = new StateSpace(null)
		s.addVertex("1")
		s.explored.add("1")
	}

	def "The node is a deadlock"() {
		expect:
		s.isDeadlock("1") == true
	}

	def "The node is explored"() {
		expect:
		s.isExplored("1") == true
	}

	def "The node is not explored"() {
		expect:
		s.isExplored("2") == false
	}

	def "The node is not a deadlock"() {
		def op = new Operation("a", "Blah", null);
		s.addEdge(op, "1", "2", EdgeType.DIRECTED);

		expect:
		s.isDeadlock("1") == false
	}
}
