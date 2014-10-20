package de.prob.statespace

import org.parboiled.common.Tuple2

import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.EvaluationCommand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.IEvalResult
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.AbstractModel

/**
 * @author joy
 *
 */
public class Trace {

	def final TraceElement current
	def final TraceElement head
	def final StateSpace stateSpace
	def final UUID UUID

	def Trace(final AbstractModel m) {
		this(m.getStateSpace())
	}

	def Trace(final StateSpace s) {
		this(s.getRoot())
	}

	def Trace(final StateId startState) {
		this.stateSpace = startState.getStateSpace()
		head = new TraceElement(startState)
		current = head
		UUID = java.util.UUID.randomUUID()
	}

	def Trace(final StateSpace s, final TraceElement head, UUID uuid) {
		this(s, head, head, uuid)
	}

	def Trace(final StateSpace s, final TraceElement head,
	final TraceElement current, UUID uuid) {
		this.stateSpace = s
		this.head = head
		this.current = current
		this.UUID = uuid
	}


	def IEvalResult evalCurrent(formula) {
		getCurrentState().eval(formula)
	}


	def List<Tuple2<String,IEvalResult>> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = stateSpace.getModel().parseFormula(f)
		}

		def List<EvaluationCommand> cmds = []

		//TODO
		def ops = head.getOpList()
		ops.each {
			if (stateSpace.canBeEvaluated(stateSpace.getVertex(it.dest))) {
				cmds << f.getCommand(stateSpace.getVertex(it.dest))
			}
		}

		ComposedCommand cmd = new ComposedCommand(cmds);
		stateSpace.execute(cmd);

		def res = []

		cmds.each {
			res << new Tuple2<String,IEvalResult>(it.getStateId(),it.getValue())
		}
		res
	}

	def Trace add(final OpInfo op) {
		// TODO: Should we check to ensure that current.getCurrentState() == op.getSrcId()
		def newHE = branch(op)

		Trace newTrace = new Trace(stateSpace, newHE, this.UUID)

		return newTrace
	}

	def Trace add(final String opId) {
		OpInfo op = getCurrentState().getOutTransitions().find { it.getId() == opId }
		if (op == null) {
			throw new IllegalArgumentException(opId
			+ " is not a valid operation on this state")
		}
		return add(op)
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
			return new Trace(stateSpace, head, current.getPrevious(), this.UUID)
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
			return new Trace(stateSpace, head, p, this.UUID)
		}
		return this
	}

	/**
	 * Determines if a branching in the list is taking place
	 * (i.e. the head of the list is not equal to the current element in the list).
	 * If branching is taking place, the new trace element will need a sublist of the
	 * list of transitions. Otherwise, the normal constructor will be used.
	 * This is used internally in this class and is a bit of a hack for performance
	 * reasons.
	 * @param op to be added to the {@link TraceElement} list
	 * @return the new {@link TraceElement}
	 */
	private TraceElement branch(OpInfo op) {
		if (head == current) {
			return new TraceElement(op, current)
		} else {
			// a new OpList is created when a branch takes place.
			// TODO: test off by one error
			def opList = new ArrayList<OpInfo>(head.getOpList().subList(0, current.getIndex()))
			opList << op
			return new TraceElement(op, current, head.getOpList().subList(0, current.getIndex()))
		}
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
		return "${current.getIndex()} previous transitions. Last executed transition: ${current.getOp().getRep()}"
	}

	def Trace randomAnimation(final int numOfSteps) {
		if (numOfSteps <= 0) {
			return this
		}

		StateId currentState = this.current.getCurrentState()
		Trace oldTrace = this
		TraceElement current = this.current
		for (int i = 0; i < numOfSteps; i++) {
			List<OpInfo> ops = currentState.getOutTransitions()
			Collections.shuffle(ops)
			OpInfo op = ops.get(0)
			if (i == 0) {
				current = branch(op) // Branch TraceElement list if necessary
			} else {
				current = new TraceElement(op, current)
			}
			currentState = op.getDestId()
		}

		Trace newTrace = new Trace(stateSpace, current, this.UUID)
		return newTrace
	}


	/**
	 * This method is included because it translates to groovy magic in a console environment
	 * (allows the execution of an event by calling it as a method in the Trace class).
	 * For executing an event from within Java, use {@link Trace#execute} instead.
	 *
	 * @param method String method name called
	 * @param params List of parameters
	 * @return {@link Trace#execute(method, params)}
	 * @deprecated use {@link Trace#execute}
	 */
	@Deprecated
	def invokeMethod(String method, params) {
		return add(getCurrentState().findTransition(method, params as List))
	}

	/**
	 * Takes an event name and a list of String predicates and uses {@link StateId#findTransition(String, List)}
	 * with the {@link Trace#currentState()}, the specified event name, and the conjunction of the parameters.
	 * If the specified operation is invalid, a runtime exception will be thrown.
	 *
	 * @param event String event name
	 * @param predicates List of String predicates to be conjoined
	 * @return {@link Trace} which is a result of executing the specified operation
	 */
	def Trace execute(String event, List<String> predicates) {
		return add(getCurrentState().findTransition(event, predicates))
	}

	def Trace execute(String event, String... predicates) {
		return execute(event, predicates as List)
	}

	/**
	 * Tests to see if the event name plus the conjunction of the parameter strings produce a valid
	 * operation on this state. Uses implementation in {@link StateSpace#isValidOperation(StateId, String, String)}
	 *
	 * @param event Name of the event to be executed
	 * @param params List of String predicates to be conjoined
	 * @return <code>true</code>, if the operation can be executed. <code>false</code>, otherwise
	 */
	def boolean canExecuteEvent(String event, List<String> params) {
		// TODO: pull this into StateId
		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")
		return stateSpace.isValidOperation(current.getCurrentState(), event, predicate);
	}

	def Trace anyOperation(filter) {
		def ops = current.getCurrentState().getOutTransitions(true)
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

	def Set<OpInfo> getNextTransitions(boolean evaluate=false) {
		return getCurrentState().getOutTransitions(evaluate)
	}

	def StateId getCurrentState() {
		return current.getCurrentState()
	}

	def StateId getPreviousState() {
		return current.getPrevious().getCurrentState()
	}

	def OpInfo getCurrentTransition() {
		return current.getOp()
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
			return head.getOpList()
		}
		throw new ClassCastException("Not able to convert Trace object to ${className}")
	}

	def List<OpInfo> getOpList(boolean evaluate=false) {
		List<OpInfo> ops = head.getOpList()
		if (evaluate) {
			stateSpace.evaluateOps(ops)
		}
		return ops
	}

	/**
	 * Takes a {@link StateSpace} and a list of {@link OpInfo} operations through the {@link StateSpace}
	 * and generates a {@link Trace} object from the information. The list of operations must be a valid
	 * list of operations starting from the root state, and for which the information has already been
	 * cached in the {@link StateSpace}. Otherwise, an assertion error will be thrown. Calls {@link Trace#addOps}
	 *
	 * @param s {@link StateSpace} through which the Trace should be generated
	 * @param ops List of {@link OpInfo} operations that should be executed in the Trace
	 * @return {@link Trace} specified by list of operations
	 */
	def static Trace getTraceFromOpList(StateSpace s, List<OpInfo> ops) {
		Trace t = new Trace(s)
		if(!ops.isEmpty()) {
			t = t.addOps(ops)
		}
		return t
	}

	/**
	 * Adds a list of operations to an existing trace.
	 *
	 * @param ops List of OpInfo objects that should be added to the current trace
	 * @return Trace with the ops added
	 */
	def Trace addOps(List<OpInfo> ops) {
		if (ops.isEmpty()) {
			return this
		}

		def first = ops.first()
		TraceElement h = branch(first) // Branch if necessary
		for (op in ops.tail()) {
			h = new TraceElement(op, h)
		}
		return new Trace(stateSpace, h, this.UUID)
	}


	/**
	 * @return an identical Trace object with a different UUID
	 */
	def Trace copy() {
		// TODO: change op list
		return new Trace(stateSpace, head, current, java.util.UUID.randomUUID())
	}
}