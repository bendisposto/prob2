package de.prob.statespace

import org.parboiled.common.Tuple2

import de.be4.classicalb.core.parser.exceptions.BException
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

	def IEvalResult evalCurrent(formula) {
		if(!stateSpace.canBeEvaluated(getCurrentState())) {
			return null
		}
		def f = formula;
		if (!(formula instanceof IEvalElement)) {
			f = stateSpace.getModel().parseFormula(f)
		}
		stateSpace.eval(getCurrentState(),[f]).get(0);
	}


	def List<Tuple2<String,IEvalResult>> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = stateSpace.getModel().parseFormula(f)
		}

		def List<EvaluationCommand> cmds = []

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

	def Trace(final AbstractModel m) {
		this(m.getStateSpace())
	}

	def Trace(final StateSpace s) {
		this.stateSpace = s
		head = new TraceElement(s.getState(s.getVertex("root")))
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
		Trace newTrace = new Trace(stateSpace, newHE, this.UUID)

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
			Trace trace = new Trace(stateSpace, head, current.getPrevious(), this.UUID)
			if(!stateSpace.isExplored(trace.getCurrentState())) {
				stateSpace.explore(trace.getCurrentState())
			}
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
			Trace trace = new Trace(stateSpace, head, p, this.UUID)
			if(!stateSpace.isExplored(trace.getCurrentState())) {
				stateSpace.explore(trace.getCurrentState())
			}
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
			ops << it.getRep()
		}
		def curTrans = current?.getOp()?.getRep() ?: "n/a"

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

	/**
	 * Deprecated. Use {@link Trace#execute }
	 * @param opName String name
	 * @param predicate String predicate
	 * @return Trace after executing specified operation
	 * @throws BException
	 */
	@Deprecated
	def Trace add(final String opName, final String predicate)
	throws BException {
		OpInfo op = findOneOp(opName, predicate)
		return add(op.id)
	}

	def String getOp(final String name, final List<String> params) {
		Set<OpInfo> outgoingEdges = stateSpace.evaluateOps(stateSpace
				.getOutEdges(current.getCurrentState()));
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

			current = new TraceElement(currentState,newState,op,previous)
			currentState = newState
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
		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")

		if(method.startsWith("\$") && !(method == "\$setup_constants" || method == "\$initialise_machine")) {
			method = method.substring(1)
		}

		OpInfo op = stateSpace.opFromPredicate(current.getCurrentState(), method, predicate , 1)[0];
		return add(op.id)
	}

	/**
	 * Takes an event name and a list of String predicates and uses {@link StateSpace#opFromPredicate}
	 * with the {@link Trace#currentState}, the specified event name, and the conjunction of the parameters.
	 * If the specified operation is invalid, a runtime exception will be thrown.
	 *
	 * @param event String event name
	 * @param params List of String predicates to be conjoined
	 * @return {@link Trace} which is a result of executing the specified operation
	 */
	def Trace execute(String event, List<String> params) {
		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")

		def ops = stateSpace.opFromPredicate(current.getCurrentState(), event, predicate , 1)
		if(ops.isEmpty()) {
			throw new IllegalArgumentException("Could not find an operation for given event and parameter combination");
		}
		OpInfo op = ops[0];
		return add(op.id)
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
		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")
		return stateSpace.isValidOperation(current.getCurrentState(), event, predicate);
	}

	def Trace anyOperation(filter) {
		def ops = new ArrayList<OpInfo>()
		ops.addAll(stateSpace.evaluateOps(stateSpace.getOutEdges(current.getCurrentState())));
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

	def ensureOpInfosEvaluated() {
		def notEvaluated = head.getOpList().findAll { !it.isEvaluated() }
		if(!notEvaluated.isEmpty()) {
			stateSpace.evaluateOps(notEvaluated);
		}
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
		TraceElement h = current
		for (op in ops) {
			def src = op.getSrcId()
			def dest = op.getDestId()
			assert src != null
			assert dest != null
			h = new TraceElement(src, dest, op, h)
		}
		stateSpace.explore(head.getCurrentState())
		return new Trace(stateSpace, h, this.UUID)
	}


	/**
	 * @return an identical Trace object with a different UUID
	 */
	def Trace copy() {
		return new Trace(stateSpace, head, current, java.util.UUID.randomUUID())
	}
}
