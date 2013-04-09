package de.prob.statespace

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.EvaluateFormulasCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvaluationResult
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.OpInfo
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel

class History {

	def final HistoryElement current
	def final HistoryElement head
	def final List<IAnimationListener> animationListeners
	def final StateSpace s

	def EvaluationResult evalCurrent(formula) {
		if(!s.canBeEvaluated(getCurrentState())) {
			return null
		}
		def f = formula;
		if (!(formula instanceof IEvalElement)) {
			f = formula as ClassicalB;
		}
		s.eval(getCurrentState(),[f]).get(0);
	}

	def List<EvaluationResult> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = formula as ClassicalB;
		}

		def List<EvaluateFormulasCommand> cmds = []

		def ops = head.getOpList()
		ops.each {
			if(s.canBeEvaluated(s.getVertex(it.dest))) {
				cmds << new EvaluateFormulasCommand([f], it.dest)
			}
		}

		ComposedCommand cmd = new ComposedCommand(cmds);
		s.execute(cmd);

		def res = []

		cmds.each {
			res << it.getValues().get(0)
		}
		res
	}


	def History(final StateSpace s) {
		this.s = s
		head = new HistoryElement(s.getState(s.getVertex("root")))
		current = head
		animationListeners = new ArrayList<IAnimationListener>()
	}

	def History(final AbstractModel m) {
		this.s = m.getStatespace()
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
		if (!s.getOutEdges(current.getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
			+ " is not a valid operation on this state")

		StateId newState = s.getState(op)

		def newHE = new HistoryElement(current.getCurrentState(), newState, op, current)
		History newHistory = new History(s, newHE,
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
	def String toString() {		return s.printOps(current.getCurrentState()) + getRep()
	}

	def String getRep() {
		if(current.getOp() == null) {
			return "";
		}
		def ops = []
		head.getOpList().each {
			ops << it.getRep(s as AbstractModel)
		}
        def curTrans = current?.getOp()?.getRep(s as AbstractModel) ?: "n/a"

		return "${ops} Current Transition is: ${curTrans}"
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
				.getOutEdges(current.getCurrentState())
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
			ops.addAll(s.getOutEdges(currentState))
			Collections.shuffle(ops)
			OpInfo op = ops.get(0)

			StateId newState = s.getState(op)
			s.evaluateFormulas(ops.get(0))

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
		def ops = new ArrayList<OpInfo>()
		ops.addAll(s.getOutEdges(current.getCurrentState()));
		if (filter != null && filter instanceof String) {
			ops=ops.findAll {
				it.name.matches(filter);
			}
		}
		if (filter != null && filter instanceof ArrayList) {
			ops=ops.findAll {
				filter.contains(it.name)
			}
		}
		Collections.shuffle(ops)
		if(!ops.empty) {
			def op = ops.get(0)
			return add(op.id)
		}
		return this
	}

	def History anyEvent(filter) {
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
		if(!animationListeners.contains(l))
			animationListeners.add(l)
	}

	def void notifyAnimationChange(final History oldHistory,final History newHistory) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(oldHistory,newHistory)
		}
	}

	def void notifyHistoryRemoval() {
		for (IAnimationListener listener : animationListeners) {
			listener.removeHistory(this)
		}
	}

	def StateSpace getStatespace() {
		return s
	}

	def Set<OpInfo> getNextTransitions() {
		return s.getOutEdges(current.getCurrentState())
	}

	def StateId getCurrentState() {
		return current.getCurrentState()
	}
	def StateId getPreviousState() {
		return current.getPrevious().getCurrentState()
	}

	def AbstractModel getModel() {
		return s.getModel()
	}

	def Object asType(Class className) {
		if(className == StateSpace) {
			return s
		}
		if(className == AbstractModel) {
			return s.model
		}
		if(className == ClassicalBModel) {
			return (ClassicalBModel) s.model
		}
		if(className == EventBModel) {
			return (EventBModel) s.model
		}
		if(className == ArrayList) {
			def list = []
			def p = head
			while(p != null) {
				list << p
				p = p.getPrevious()
			}
			return list.reverse()
		}
		throw new ClassCastException("Not able to convert History object to ${className}")
	}
}
