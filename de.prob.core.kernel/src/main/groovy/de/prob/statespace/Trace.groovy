package de.prob.statespace

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.EvaluateFormulasCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvaluationResult
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel

class Trace {

	def final TraceElement current
	def final TraceElement head
	def final StateSpace stateSpace

	def EvaluationResult evalCurrent(formula) {
		if(!stateSpace.canBeEvaluated(getCurrentState())) {
			return null
		}
		def f = formula;
		if (!(formula instanceof IEvalElement)) {
			f = formula as ClassicalB;
		}
		stateSpace.eval(getCurrentState(),[f]).get(0);
	}

	def List<EvaluationResult> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = formula as ClassicalB;
		}

		def List<EvaluateFormulasCommand> cmds = []

		def ops = head.getOpList()
		ops.each {
			if(stateSpace.canBeEvaluated(stateSpace.getVertex(it.dest))) {
				cmds << new EvaluateFormulasCommand([f], it.dest)
			}
		}

		ComposedCommand cmd = new ComposedCommand(cmds);
		stateSpace.execute(cmd);

		def res = []

		cmds.each {
			res << it.getValues().get(0)
		}
		res
	}


	def Trace(final StateSpace s) {
		this.stateSpace = s
		head = new TraceElement(s.getState(s.getVertex("root")))
		current = head
	}

	def Trace(final AbstractModel m) {
		this.stateSpace = m.getStatespace()
		head = new TraceElement(s.getState(s.getVertex("root")))
		current = head
	}

	def Trace(final StateSpace s, final TraceElement head) {
		this.stateSpace = s
		this.head = head
		this.current = head
	}

	def Trace(final StateSpace s, final TraceElement head,
	final TraceElement current) {
		this.stateSpace = s
		this.head = head
		this.current = current
	}

	def Trace add(final String name, final List<String> params) {
		return add(getOp(name, params))
	}

	def Trace add(final String opId) {
		OpInfo op = stateSpace.getOps().get(opId)
		if (!stateSpace.getOutEdges(current.getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
			+ " is not a valid operation on this state")

		StateId newState = stateSpace.getState(op)

		def newHE = new TraceElement(current.getCurrentState(), newState, op, current)
		Trace newTrace = new Trace(stateSpace, newHE)

		return newTrace
	}

	def Trace add(final int i) {
		String opId = String.valueOf(i)
		return add(opId)
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	def Trace back() {
		if (canGoBack()) {
			Trace trace = new Trace(stateSpace, head, current.getPrevious())
			return trace
		}
		return this
	}


	/**
	 * Moves one step forward in the animation if this is possible
	 *
	 * @return
	 */
	def Trace forward() {
		if (canGoForward()) {
			TraceElement p = head
			while (p.getPrevious() != current) {
				p = p.getPrevious()
			}
			Trace trace = new Trace(stateSpace, head, p)
			return trace
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
		return stateSpace.printOps(current.getCurrentState()) + getRep()
	}

	def String getRep() {
		if(current.getOp() == null) {
			return "";
		}
		def ops = []
		head.getOpList().each {
			ops << it.getRep(stateSpace as AbstractModel)
		}
		def curTrans = current?.getOp()?.getRep(stateSpace as AbstractModel) ?: "n/a"

		return "${ops} Current Transition is: ${curTrans}"
	}

	def OpInfo findOneOp(final String opName, final String predicate)
	throws BException {
		List<OpInfo> ops = stateSpace.opFromPredicate(current.getCurrentState(), opName,
				predicate, 1)
		if (!ops.isEmpty())
			return ops.get(0)
		throw new IllegalArgumentException("Operation with name " + opName
		+ " not found.")
	}

	def Trace add(final String opName, final String predicate)
	throws BException {
		OpInfo op = findOneOp(opName, predicate)
		return add(op.id)
	}

	def String getOp(final String name, final List<String> params) {
		Set<OpInfo> outgoingEdges = stateSpace
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

	def Trace randomAnimation(final int numOfSteps) {
		StateId currentState = this.current.getCurrentState()
		Trace oldTrace = this
		TraceElement previous = this.current
		TraceElement current = this.current
		for(int i = 0; i < numOfSteps; i++) {
			previous = current
			List<OpInfo> ops = new ArrayList<OpInfo>()
			ops.addAll(stateSpace.getOutEdges(currentState))
			Collections.shuffle(ops)
			OpInfo op = ops.get(0)

			StateId newState = stateSpace.getState(op)
			stateSpace.explore(ops.get(0))

			current = new TraceElement(currentState,newState,op,previous)
			currentState = newState
		}

		Trace newTrace = new Trace(stateSpace, current)
		return newTrace
	}

	def Trace invokeMethod(String method,  params) {
		String predicate;

		if(method.startsWith("\$")) {
			method = method.substring(1)
		}

		if (params == []) predicate = "TRUE = TRUE"
		else predicate = params[0];
		OpInfo op = stateSpace.opFromPredicate(current.getCurrentState(), method,predicate , 1)[0];
		return add(op.id)
	}

	def Trace anyOperation(filter) {
		def ops = new ArrayList<OpInfo>()
		ops.addAll(stateSpace.getOutEdges(current.getCurrentState()));
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

	def Trace anyEvent(filter) {
		anyOperation(filter);
	}

	def StateSpace getStateSpace() {
		return stateSpace
	}

	def Set<OpInfo> getNextTransitions() {
		return stateSpace.getOutEdges(current.getCurrentState())
	}

	def StateId getCurrentState() {
		return current.getCurrentState()
	}
	def StateId getPreviousState() {
		return current.getPrevious().getCurrentState()
	}

	def AbstractModel getModel() {
		return stateSpace.getModel()
	}

	def Object asType(Class className) {
		if(className == StateSpace) {
			return stateSpace
		}
		if(className == AbstractModel) {
			return stateSpace.model
		}
		if(className == ClassicalBModel) {
			return (ClassicalBModel) stateSpace.model
		}
		if(className == EventBModel) {
			return (EventBModel) stateSpace.model
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
		throw new ClassCastException("Not able to convert Trace object to ${className}")
	}
}
