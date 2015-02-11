package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Ignore
import spock.lang.Specification
import de.prob.animator.IAnimator
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.AbstractModel


//TODO: Retest??
@Ignore
class StateSpaceTest extends Specification {

	private class MyProvider<E> implements com.google.inject.Provider<E> {

		def animator

		def MyProvider(animator) {
			this.animator = animator
		}

		@Override
		public E get() {
			return animator
		}
	}

	def class Model extends AbstractModel {
		def FormalismType getFormalismType() {
			return FormalismType.B
		}

		@Override
		public AbstractElement getMainComponent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IEvalElement parseFormula(String formula) {
			// TODO Auto-generated method stub
			return null;
		}
	}


	def StateSpace s

	def setup() {

		def mock = mock(IAnimator.class)
		//		doThrow(new ProBError("XXX")).when(mock).execute(any(Object.class));

		s = new StateSpace(new MyProvider<IAnimator>(mock))
		s.setModel(new Model())

		def states = [
			new State("1",s),
			new State("root", s),
			new State("2", s),
			new State("3", s),
			new State("4", s),
			new State("5", s),
			new State("6", s)
		]

		def ops = [
			new Transition(s, "b","root","2"),
			new Transition(s, "c","2","3"),
			new Transition(s, "d","3","4"),
			new Transition(s, "e","3","5"),
			new Transition(s, "f","4","6")
		]

		states.each { it ->
			s.addVertex(it)
		}
		states.each { it ->
			s.states.put(it.getId(),it)
		}
		s.explored.add(s.states.get("1"))

		ops.each {
			s.ops.put(it.getId(),it)
		}

		s.addEdge(s.ops.get("b"), s.states.get("root"), s.states.get("2"))
		s.addEdge(s.ops.get("c"), s.states.get("2"), s.states.get("3"))
		s.addEdge(s.ops.get("d"), s.states.get("3"), s.states.get("4"))
		s.addEdge(s.ops.get("e"), s.states.get("3"), s.states.get("5"))
		s.explored.add(s.states.get("2"))
		s.explored.add(s.states.get("3"))
		s.explored.add(s.states.get("4"))
		s.explored.add(s.states.get("5"))

		s.addEdge(s.ops.get("f"), s.states.get("4"), s.states.get("6"))
	}

	def addVertices(List<String> ids, StateSpace s) {
		for (String id : ids) {
			s.addVertex(new State(id))
		}
	}



	def "The node is a deadlock"() {
		expect:
		s.isDeadlock(s[1]) == true
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
		def a = new State("7", s)
		s.addVertex(a)
		s.states.put(a.getId(),a)

		then:
		s.isExplored(s.states.get("7")) == false
	}

	def "The node is not a deadlock"() {
		s.addEdge(new Transition(s,"bla","1","2"), s.states.get("1"), s.states.get("2"))

		expect:
		s.isDeadlock(s[1]) == false
	}

	def "test register StateSpaceChangeListener"() {
		when:
		def l = mock(IStatesCalculatedListener.class)
		s.registerStateSpaceListener(l)

		then:
		s.stateSpaceListeners.contains(l)
	}
}
