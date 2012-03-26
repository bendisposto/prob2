package de.prob.model


import static org.junit.Assert.*

import org.spockframework.compiler.model.ExpectBlock;

import spock.lang.Specification
import de.prob.ProBException;
import de.prob.model.StateSpace
import de.prob.animator.IAnimator
import edu.uci.ics.jung.graph.util.EdgeType;
import de.prob.model.Operation
import de.prob.animator.AnimatorImpl
import de.prob.animator.command.ICommand;

class StateSpaceTest extends Specification {

	private class NewAnimator implements IAnimator {
		public void execute(ICommand command) throws ProBException {
			throw new ProBException();
		}
		public void execute(ICommand... commands) throws ProBException {
			throw new ProBException();
		}
	}

	def StateSpace s

	def setup() {

		s = new StateSpace(new NewAnimator())

		s.addVertex("1")
		s.explored.add("1")

		s.addEdge("b","root","2")
		s.addEdge("c","2","3")
		s.addEdge("d","3","4")
		s.addEdge("e","3","5")

		s.history.add("b")
		s.history.add("c")

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

	def "The node is not explored"() {
		expect:
		s.isExplored("7") == false
	}

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
}
