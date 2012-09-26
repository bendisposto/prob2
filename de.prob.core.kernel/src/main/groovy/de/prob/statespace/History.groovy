package de.prob.statespace

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Set

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.domainobjects.OpInfo

class History {

	def final HistoryElement current
	def final HistoryElement head
	def final List<IAnimationListener> animationListeners
	def final StateSpace s

	def History(final StateSpace s) {
		this.s = s
		head = new HistoryElement(s.getState(s.getVertex("root")))
		current = head
		animationListeners = new ArrayList<IAnimationListener>()
	}

	def History(final StateSpace s, final HistoryElement head,
	final List<IAnimationListener> animationListeners) {
		this.s = s
		this.head = head
		this.current = head
		this.animationListeners = animationListeners
	}

	def History(final StateSpace s, final HistoryElement head,
	final HistoryElement current,
	final List<IAnimationListener> animationListeners) {
		this.s = s
		this.head = head
		this.current = current
		this.animationListeners = animationListeners
	}

	def History add(final String name, final List<String> params) {
		return add(getOp(name, params))
	}

	def History add(final String opId) {
		OpInfo op = s.getOps().get(opId)
		if (!s.outgoingEdgesOf(current.getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
			+ " is not a valid operation on this state")

		StateId newState = s.getState(op)
		s.evaluateFormulas(current.getCurrentState())

		History newHistory = new History(s, new HistoryElement(
				current.getCurrentState(), newState, op, current),
				animationListeners)

		return newHistory
	}

	def History add(final int i) {
		String opId = String.valueOf(i)
		return add(opId)
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	def History back() {
		if (canGoBack()) {
			History history = new History(s, head, current.getPrevious(),
					animationListeners)
			return history
		}
		return this
	}

	/**
	 * Moves one step forward in the animation if this is possible
	 *
	 * @return
	 */
	def History forward() {
		if (canGoForward()) {
			HistoryElement p = head
			while (p.getPrevious() != current) {
				p = p.getPrevious()
			}
			History history = new History(s, head, p, animationListeners)
			return history
		}
		return this
	}

	def boolean canGoForward() {
		return current != head
	}

	def boolean canGoBack() {
		return current.getPrevious() != null
	}

	@Override
	def String toString() {
		return s.printOps(current.getCurrentState()) + getRep()
	}

	def String getRep() {
		return head.getRepresentation() + "] Current Transition is: "
		+ current.getOp()
	}

	def OpInfo findOneOp(final String opName, final String predicate)
	throws BException {
		List<OpInfo> ops = s.opFromPredicate(current.getCurrentState(), opName,
				predicate, 1)
		if (!ops.isEmpty())
			return ops.get(0)
		throw new IllegalArgumentException("Operation with name " + opName
		+ " not found.")
	}

	def History add(final String opName, final String predicate)
	throws BException {
		OpInfo op = findOneOp(opName, predicate)
		return add(op.id)
	}

	def String getOp(final String name, final List<String> params) {
		Set<OpInfo> outgoingEdges = s
				.outgoingEdgesOf(current.getCurrentState())
		String id = null
		for (OpInfo op : outgoingEdges) {
			if (op.getName().equals(name) && op.getParams().equals(params)) {
				id = op.getId()
				break
			}
		}
		return id
	}

	def History randomAnimation(final int numOfSteps) {
		StateId currentState = this.current.getCurrentState()
		History oldHistory = this
		HistoryElement previous = this.current
		HistoryElement current = this.current
		for(int i = 0; i < numOfSteps; i++) {
			previous = current
			List<OpInfo> ops = new ArrayList<OpInfo>()
			ops.addAll(s.outgoingEdgesOf(currentState))
			Collections.shuffle(ops)
			OpInfo op = ops.get(0)

			StateId newState = s.getState(op)
			s.evaluateFormulas(newState)

			current = new HistoryElement(currentState,newState,op,previous)
			currentState = newState
		}

		History newHistory = new History(s, current, animationListeners)
		return newHistory
	}
	
	def History invokeMethod(String method,  params) {
		String predicate;
		
		if(method.startsWith("\$")) {
			method = method.substring(1)
		}
		
		if (params == []) predicate = "TRUE = TRUE"
		else predicate = params[0];
		OpInfo op = s.opFromPredicate(current.getCurrentState(), method,predicate , 1)[0];
		return add(op.id)
	}
	
	def History anyOperation(filter) {
		def spaceInfo = s.info
		def ops = new ArrayList()
		ops.addAll(s.outgoingEdgesOf(current.getCurrentState()));
		if (filter != null && filter instanceof String) {
			ops=ops.findAll {
				def opinfo = spaceInfo.getOp(it)
				def name = opinfo.getName()
				name.matches(filter);
			}
		}
		if (filter != null && filter instanceof ArrayList) {
			ops=ops.findAll {
				def opinfo = spaceInfo.getOp(it)
				def name = opinfo.getName()
				filter.contains(name)
			}
		}
		Collections.shuffle(ops)
		def op = ops.get(0)
		return add(op.id)
	}
	
	def anyEvent(filter) {
		anyOperation(filter);
	}

	/**
	 * Adds an IAnimationListener to the list of animationListeners. This
	 * listener will be notified whenever an animation step is performed
	 * (whenever the current state changes).
	 *
	 * @param l
	 */
	def void registerAnimationListener(final IAnimationListener l) {
		animationListeners.add(l)
	}

	def void notifyAnimationChange(final History oldHistory,final History newHistory) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(oldHistory,newHistory)
		}
	}

	def StateSpace getStatespace() {
		return s
	}

	def Set<OpInfo> getNextTransitions() {
		return s.outgoingEdgesOf(current.getCurrentState())
	}
}
