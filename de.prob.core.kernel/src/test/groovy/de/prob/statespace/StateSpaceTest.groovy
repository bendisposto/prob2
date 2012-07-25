package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.animator.IAnimator
import de.prob.animator.domainobjects.OpInfo
import de.prob.exception.ProBError

class StateSpaceTest extends Specification {


	def StateSpace s

	def setup() {

		def mock = mock(IAnimator.class)

		doThrow(new ProBError("XXX")).when(mock).execute(any(Object.class));

		s = new StateSpace(mock, new DirectedMultigraphProvider(), new StateSpaceInfo())

		def states = [
			new StateId("1", "lorem",s),
			new StateId("root", "lorem2",s),
			new StateId("2", "ipsum",s),
			new StateId("3", "dolor",s),
			new StateId("4", "sit",s),
			new StateId("5", "amit",s),
			new StateId("6", "consetetur",s)
		]

		def ops = [ new OpInfo("b","b","root","2",[],"2"),
			new OpInfo("c","c","2","3",[],"3"),
			new OpInfo("d","d","3","4",[],"4"),
			new OpInfo("e","e","3","5",[],"5"),
			new OpInfo("f","f","4","6",[],"6") ]

		states.each {
			it ->
			s.addVertex(it)
		}
		states.each {
			it ->
			s.states.put(it.getId(),it)
		}
		s.explored.add(s.states.get("1"))

		ops.each {
			s.ops.put(it.getId(),it)
		}

		s.addEdge(s.states.get("root"), s.states.get("2"), s.ops.get("b"))
		s.addEdge(s.states.get("2"), s.states.get("3"), s.ops.get("c"))
		s.addEdge(s.states.get("3"), s.states.get("4"), s.ops.get("d"))
		s.addEdge(s.states.get("3"), s.states.get("5"), s.ops.get("e"))
		s.explored.add(s.states.get("2"))
		s.explored.add(s.states.get("3"))
		s.explored.add(s.states.get("4"))
		s.explored.add(s.states.get("5"))

		s.addEdge(s.states.get("4"), s.states.get("6"),s.ops.get("f"))
	}

	def addVertices(List<String> ids, StateSpace s) {
		for (String id : ids) {
			s.addVertex(new StateId(id))
		}
	}



	def "The node is a deadlock"() {
		expect:
		s.isDeadlock("1") == true
	}

	def "The node is explored"() {
		expect:
		s.isExplored(s.states.get("1")) == true
	}

	def "The stateid is unknown"() {
		when:
		s.isExplored(s.states.get("7"))

		then:
		thrown IllegalArgumentException
	}

	def "The state is not explored"() {
		when:
		def a = new StateId("7", "bla",s)
		s.addVertex(a)
		s.states.put(a.getId(),a)

		then:
		s.isExplored(s.states.get("7")) == false
	}

	def "The node is not a deadlock"() {
		s.addEdge(s.states.get("1"), s.states.get("2"), new OpInfo("bla","blah","1","2",[],"2"))

		expect:
		s.isDeadlock("1") == false
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
