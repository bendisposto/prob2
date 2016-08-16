package de.prob.statespace

import com.github.krukow.clj_lang.PersistentVector

import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.EvaluationCommand
import de.prob.animator.domainobjects.AbstractEvalResult
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.representation.AbstractModel
import de.prob.util.Tuple2
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * @author joy
 *
 */
public class Trace {

	def static boolean exploreStateByDefault = true
	def final TraceElement current
	def final TraceElement head
	def final StateSpace stateSpace
	def final UUID UUID
	def final List<Transition> transitionList

	@SuppressWarnings(value="RANGE_ARRAY_INDEX", justification="False positive, Findbugs vs. Groovy")
	def Trace(final StateSpace s) {
		this(s.getRoot())
	}

	def Trace(final State startState) {
		this(startState.getStateSpace(), new TraceElement(startState), PersistentVector.emptyVector(), java.util.UUID.randomUUID())
	}

	def Trace(final StateSpace s, final TraceElement head, List<Transition> transitionList, UUID uuid) {
		this(s, head, head, transitionList, uuid)
	}

	private Trace(final StateSpace s, final TraceElement head,
	final TraceElement current, List<Transition> transitionList, UUID uuid) {
		this.stateSpace = s
		this.head = head
		this.current = current
		this.transitionList = transitionList
		this.UUID = uuid
	}


	def AbstractEvalResult evalCurrent(formula) {
		return getCurrentState().eval(formula)
	}

	def int size() {
		return transitionList.size();
	}

	def List<Tuple2<String,AbstractEvalResult>> eval(formula) {
		def f = formula;
		if(!(formula instanceof IEvalElement)) {
			f = stateSpace.getModel().parseFormula(f)
		}

		def List<EvaluationCommand> cmds = []
		transitionList.each {
			if (stateSpace.canBeEvaluated(it.getDestination())) {
				cmds << f.getCommand(it.getDestination())
			}
		}

		ComposedCommand cmd = new ComposedCommand(cmds);
		stateSpace.execute(cmd);

		def res = []

		cmds.each {
			res << new Tuple2<String,AbstractEvalResult>(it.getStateId(),it.getValue())
		}
		res
	}

	def Trace add(final Transition op) {
		// TODO: Should we check to ensure that current.getCurrentState() == op.getSrcId()
		def newHE = new TraceElement(op, current)
		def transitionList = branchTransitionListIfNecessary(op)
		Trace newTrace = new Trace(stateSpace, newHE, transitionList, this.UUID)
		if (exploreStateByDefault && !op.getDestination().isExplored()) {
			op.getDestination().explore()
		}
		return newTrace
	}

	def Trace add(final String transitionId) {
		Transition op = getCurrentState().getOutTransitions().find { it.getId() == transitionId }
		if (op == null) {
			throw new IllegalArgumentException(transitionId
			+ " is not a valid operation on this state")
		}
		return add(op)
	}

	def Trace add(final int i) {
		String opId = String.valueOf(i)
		return add(opId)
	}

	/**
	 * Tries to find an operation with the specified name and parameters in the
	 * list of transitions calculated by ProB.
	 * @param name of the event to be executed
	 * @param parameters values of the parameters for the event
	 * @return a new trace with the operation added.
	 */
	def Trace addTransitionWith(String name, List<String> parameters) {
		def op = getCurrentState().getOutTransitions(true).find { it.getName() == name && it.getParams() == parameters }
		if (op == null) {
			throw new IllegalArgumentException("Could find operation "+name+" with parameters "+parameters.toString());
		}
		return add(op);
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	def Trace back() {
		if (canGoBack()) {
			return new Trace(stateSpace, head, current.getPrevious(), transitionList, this.UUID)
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
			return new Trace(stateSpace, head, p, transitionList, this.UUID)
		}
		return this
	}


	def Trace gotoPosition(int pos) {
		def trace = this;
		int currentIndex = trace.getCurrent().getIndex();
		if (pos == currentIndex) {
			return trace;
		} else if (pos > currentIndex && pos < size()) {
			while (!(pos == trace.getCurrent().getIndex())) {
				trace = trace.forward();
			}
		} else if (pos < currentIndex && pos >= -1) {
			while (!(pos == trace.getCurrent().getIndex())) {
				trace = trace.back();
			}
		}
		return trace;
	}

	def boolean canGoForward() {
		return current != head
	}

	def boolean canGoBack() {
		return current.getPrevious() != null
	}

	private PersistentVector<Transition> branchTransitionListIfNecessary(Transition newOp) {
		if (head == current) {
			return transitionList.assocN(transitionList.size(), newOp)
		} else {
			def tList = PersistentVector.create(transitionList.subList(0, current.getIndex() + 1))
			return tList.assocN(tList.size(), newOp)
		}
	}

	@Override
	def String toString() {
		return stateSpace.printOps(current.getCurrentState()) + getRep()
	}

	def String getRep() {
		if(current.getTransition() == null) {
			return "";
		}
		return "${current.getIndex()} previous transitions. Last executed transition: ${current.getTransition().evaluate(FormulaExpand.truncate).getRep()}"
	}

	def Trace randomAnimation(final int numOfSteps) {
		if (numOfSteps <= 0) {
			return this
		}

		State currentState = this.current.getCurrentState()
		Trace oldTrace = this
		TraceElement current = this.current
		PersistentVector<Transition> transitionList = this.transitionList
		for (int i = 0; i < numOfSteps; i++) {
			List<Transition> ops = currentState.getOutTransitions()
			Collections.shuffle(ops)
			Transition op = ops.get(0)
			current = new TraceElement(op, current)
			if (i == 0) {
				transitionList = branchTransitionListIfNecessary(op)
			} else {
				transitionList = transitionList.assocN(transitionList.size(), op)
			}
			currentState = op.getDestination()
		}

		Trace newTrace = new Trace(stateSpace, current, transitionList, this.UUID)
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
		if(method.startsWith("\$") && !(method == "\$setup_constants" || method == "\$initialise_machine")) {
			method = method.substring(1)
		}
		def transition = getCurrentState().findTransition(method, params as List)
		if (transition == null) {
			throw new IllegalArgumentException("Could not execute event with name "+method+" and parameters "+params.toString());
		}
		return add(transition)
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
		def transition = getCurrentState().findTransition(event, predicates as List)
		if (transition == null) {
			throw new IllegalArgumentException("Could not execute event with name "+event+" and parameters "+predicates.toString());
		}
		return add(transition)
	}

	def Trace execute(String event, String... predicates) {
		return execute(event, predicates as List)
	}

	/**
	 * Tests to see if the event name plus the conjunction of the parameter strings produce a valid
	 * operation on this state. Uses implementation in {@link Trace#canExecuteEvent(String, List)}
	 *
	 * @param event Name of the event to be executed
	 * @param predicates to be conjoined
	 * @return <code>true</code>, if the operation can be executed. <code>false</code>, otherwise
	 */
	def boolean canExecuteEvent(String event, String... predicates) {
		return canExecuteEvent(event, predicates as List)
	}

	/**
	 * Tests to see if the event name plus the conjunction of the parameter strings produce a valid
	 * operation on this state. Uses implementation in {@link StateSpace#isValidOperation(StateId, String, String)}
	 *
	 * @param event Name of the event to be executed
	 * @param predicates List of String predicates to be conjoined
	 * @return <code>true</code>, if the operation can be executed. <code>false</code>, otherwise
	 */
	def boolean canExecuteEvent(String event, List<String> predicates) {
		return getCurrentState().findTransition(event, predicates) != null
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

	def Set<Transition> getNextTransitions(boolean evaluate=false, FormulaExpand expansion=FormulaExpand.truncate) {
		return getCurrentState().getOutTransitions(evaluate, expansion)
	}

	def State getCurrentState() {
		return current.getCurrentState()
	}

	def State getPreviousState() {
		return current.getPrevious().getCurrentState()
	}

	def Transition getCurrentTransition() {
		return current.getTransition()
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
		if(className == stateSpace.model.getClass()) {
			return stateSpace.model
		}
		throw new ClassCastException("Not able to convert Trace object to ${className}")
	}

	def List<Transition> getTransitionList(boolean evaluate=false, FormulaExpand expansion=FormulaExpand.truncate) {
		List<Transition> ops = transitionList
		if (evaluate) {
			stateSpace.evaluateTransitions(ops, expansion)
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
	def static Trace getTraceFromTransitions(StateSpace s, List<Transition> ops) {
		if(!ops.isEmpty()) {
			Trace t = new Trace(ops.first().getSource())
			t = t.addTransitions(ops)
			return t
		}
		return new Trace(s)
	}

	/**
	 * Adds a list of operations to an existing trace.
	 *
	 * @param ops List of OpInfo objects that should be added to the current trace
	 * @return Trace with the ops added
	 */
	def Trace addTransitions(List<Transition> ops) {
		if (ops.isEmpty()) {
			return this
		}

		def first = ops.first()
		TraceElement h = new TraceElement(first, current)
		def transitionList = branchTransitionListIfNecessary(first)
		for (op in ops.tail()) {
			h = new TraceElement(op, h)
			transitionList = transitionList.assocN(transitionList.size(), op)
		}
		return new Trace(stateSpace, h, transitionList, this.UUID)
	}


	/**
	 * @return an identical Trace object with a different UUID
	 */
	def Trace copy() {
		return new Trace(stateSpace, head, current, transitionList, java.util.UUID.randomUUID())
	}
}