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

	def final int currentPos
	def final StateId currentState
	def final StateSpace stateSpace
	def final UUID UUID
	def final ArrayList<OpInfo> opList

	def Trace(final AbstractModel m) {
		this(m.getStateSpace())
	}

	def Trace(final StateSpace s) {
		this(s.getRoot())
	}

	def Trace(StateId startState) {
		this(startState, [], UUID.randomUUID())
	}

	def Trace(StateId currentState, List<OpInfo> opList, UUID uuid) {
		this(currentState, opList, opList.size() - 1, uuid)
	}

	def Trace(StateId currentState, List<OpInfo> opList, int currentPos, UUID uuid) {
		this.stateSpace = currentState.getStateSpace()
		this.currentState = currentState
		this.opList = opList
		this.currentPos = currentPos
		this.UUID = uuid
	}


	def IEvalResult evalCurrent(formula) {
		currentState.eval(formula)
	}


	def List<Tuple2<String,IEvalResult>> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = stateSpace.getModel().parseFormula(f)
		}

		def List<EvaluationCommand> cmds = []
		opList.each {
			if (it.getDestId().isInitialised()) {
				cmds << f.getCommand(it.getDestId())
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

	def Trace add(final OpInfo transition) {
		StateId newState = transition.getDestId()
		List<OpInfo> newOps = new ArrayList<OpInfo>(opList)
		newOps << transition

		Trace newTrace = new Trace(newState, newOps, this.UUID)
	}

	def Trace add(final String opId) {
		OpInfo op = currentState.getOutTransitions().find { it.getId() == opId }
		if (op == null)
			throw new IllegalArgumentException(opId
			+ " is not a valid operation on this state")

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
			StateId newState = opList[currentPos].getSrcId().explore()
			Trace trace = new Trace(newState, new ArrayList<OpInfo>(opList), currentPos - 1, this.UUID)
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
			StateId newState = opList[currentPos].getDestId().explore()
			Trace trace = new Trace(newState, new ArrayList<OpInfo>(opList), currentPos + 1, this.UUID)
			return trace
		}
		return this
	}

	def boolean canGoForward() {
		return currentPos < (opList.size() - 1)
	}

	def boolean canGoBack() {
		return currentPos > -1
	}

	@Override
	def String toString() {
		return stateSpace.printOps(currentState) + getRep()
	}

	def String getRep() {
		if(opList.isEmpty()) {
			return "";
		}
		def ops = opList.collect { it.getRep() }
		def curTrans = currentPos > -1 ? opList[currentPos] : "n/a"

		return "${ops} Current Transition is: ${curTrans}"
	}

	def OpInfo findOneOp(final String opName, final String predicate)
	throws BException {
		List<OpInfo> ops = stateSpace.opFromPredicate(currentState, opName,
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
		Set<OpInfo> outgoingEdges = stateSpace.evaluateOps(currentState.getOutTransitions())
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
		Trace oldTrace = this
		List<OpInfo> newOps = new ArrayList<OpInfo>(opList)
		StateId state = currentState
		for(int i = 0; i < numOfSteps; i++) {
			List<OpInfo> ops = currentState.getOutTransitions()
			Collections.shuffle(ops)
			OpInfo op = ops.get(0)
			state = op.getDestId()
			newOps << op
		}

		Trace newTrace = new Trace(state, newOps, this.UUID)
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
		return add(currentState.findTransition(method, params))
	}

	/**
	 * Takes an event name and a list of String predicates and uses {@link StateSpace#opFromPredicate}
	 * with the {@link Trace#currentState}, the specified event name, and the conjunction of the parameters.
	 * If the specified operation is invalid, a runtime exception will be thrown.
	 *
	 * @param event String event name
	 * @param predicates List of String predicates to be conjoined
	 * @return {@link Trace} which is a result of executing the specified operation
	 */
	def Trace execute(String event, List<String> predicates) {
		return add(currentState.findTransition(event, predicates))
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
		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")
		return stateSpace.isValidOperation(currentState, event, predicate);
	}

	def Trace anyOperation(filter) {
		def ops = currentState.getOutTransitions(true)
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
		return currentState.getOutTransitions()
	}

	def OpInfo getCurrentTransition() {
		return currentPos > -1 ? opList[currentPos] : null
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
		throw new ClassCastException("Not able to convert Trace object to ${className}")
	}

	/**
	 * Access the list of operations that have been executed. By default, the list of
	 * {@link OpInfo} objects will not be evaluated (i.e. certain information about the
	 * transition will be lazily retrieved from ProB at a later time). However, if an
	 * optional parameter is supplied and set to true, the evaluation of all of the
	 * {@link OpInfo} objects will occur before the list is returned via the
	 * {@link StateSpace#evaluateOps(Collection)} method.
	 * @param evaluate a boolean flag to determine if the ops should be evaluated or not
	 * @return the list of {@link OpInfo}s that have been evaluated over the course of the {@link Trace}
	 */
	def List<OpInfo> getOpList(boolean evaluate=false) {
		if (evaluate) {
			stateSpace.evaluateOps(opList)
		}
		return opList
	}

	/**
	 * Takes a {@link StateSpace} and a list of {@link OpInfo} operations through the {@link StateSpace}
	 * and generates a {@link Trace} object from the information. The list of operations must be a valid
	 * list of operations. The {@link Trace} object produced will start from the first state in the list
	 * of transitions. Calls {@link Trace#addOps}
	 *
	 * @param s {@link StateSpace} through which the Trace should be generated
	 * @param ops List of {@link OpInfo} operations that should be executed in the Trace
	 * @return {@link Trace} specified by list of operations
	 */
	def static Trace getTraceFromOpList(StateSpace s, List<OpInfo> ops) {
		if (ops.isEmpty()) {
			return new Trace(s)
		}
		return new Trace(ops.first().getSrcId()).addOps(ops)
	}

	/**
	 * Adds a list of operations to an existing trace.
	 *
	 * @param ops List of OpInfo objects that should be added to the current trace
	 * @return Trace with the ops added
	 */
	def Trace addOps(List<OpInfo> ops) {
		List<OpInfo> newOps = new ArrayList<OpInfo>(opList)
		newOps.addAll(ops)
		StateId stateId = newOps.last().getDestId().explore()
		return new Trace(stateId, newOps, this.UUID)
	}


	/**
	 * @return an identical Trace object with a different UUID
	 */
	def Trace copy() {
		return new Trace(currentState, new ArrayList<OpInfo>(opList), currentPos,  java.util.UUID.randomUUID())
	}
}
