package de.prob.statespace

import com.google.common.base.Objects

import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.EvaluateRegisteredFormulasCommand
import de.prob.animator.command.ExploreStateCommand
import de.prob.animator.command.GetBStateCommand
import de.prob.animator.domainobjects.AbstractEvalResult
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.StateError
import de.prob.util.StringUtil


/**A reference to the state object in the ProB core.
 *
 * Note: This class contains a reference to the StateSpace object to which this state
 * reference belongs. In order for the garbage collector to work correctly, dereference
 * any StateId objects after they are no longer needed.
 *
 * @author joy
 */
class State {

	protected def String id;
	def StateSpace stateSpace;
	def boolean explored
	def List<Transition> transitions = []
	def private boolean initialised
	def private boolean invariantOk
	def private boolean timeoutOccurred
	def private Set<String> transitionsWithTimeout
	def private boolean maxTransitionsCalculated
	def private Collection<StateError> stateErrors
	def Map<IEvalElement, AbstractEvalResult> values = new HashMap<IEvalElement, AbstractEvalResult>()

	def State(String id, StateSpace space) {
		this.id = id;
		this.explored = false
		this.stateSpace = space
	}

	/**
	 * This method is included for groovy magic in a console environment.
	 * Use the {@link StateId#perform} method instead.
	 *
	 * @param method String method name that was called
	 * @param params List of parameter objects that it was called with
	 * @return result of {@link StateId#perform}
	 * @deprecated use {@link StateId#perform}
	 */
	@Deprecated
	def invokeMethod(String method,  params) {
		if(method.startsWith("\$") && !(method == "\$setup_constants" || method == "\$initialise_machine")) {
			method = method.substring(1)
		}

		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")
		Transition op = stateSpace.transitionFromPredicate(this, method, predicate , 1)[0];
		transitions << op
		return op.getDestination();
	}

	/**
	 * Uses {@link StateId#perform(String,List)} to calculate the destination state of the event
	 * with the specified event name and the conjunction of the parameters.
	 * An exception will be thrown if the specified event and params are invalid for this StateId.
	 * @param event String event name to execute
	 * @param params List of String predicates
	 * @return {@link StateId} that results from executing the specified event
	 */
	def State perform(String event, String... predicates) {
		return perform(event, predicates as List)
	}

	/**
	 * Uses {@link StateId#findTransition(String,List)} to calculate the destination state of the event
	 * with the specified event name and the conjunction of the parameters.
	 * An exception will be thrown if the specified event and predicates are invalid for this StateId.
	 * @param event String event name to execute
	 * @param params List of String predicates
	 * @return {@link StateId} that results from executing the specified event
	 */
	def State perform(String event, List<String> predicates) {
		def op = findTransition(event, predicates)
		if (op == null) {
			throw new IllegalArgumentException("Could not execute "+event+" with predicates "+predicates.toString() + " on state "+this.getId())
		}
		return op.getDestination();
	}

	/**
	 * Calls {@link StateId#findTransition(String, List)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @return the calculated transition, or null if no transition was found.
	 */
	def Transition findTransition(String name, String... predicates) {
		return findTransition(name, predicates as List)
	}

	/**
	 * Calls {@link StateId#findTransitions(String, List, Integer)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @return the calculated transition, or null if no transition was found.
	 */
	def Transition findTransition(String name, List<String> predicates) {
		if (predicates.isEmpty() && !transitions.isEmpty()) {
			def op = transitions.find { it.getName() == name }
			if (op != null) {
				return op
			}
		}
		def transitions = findTransitions(name, predicates, 1)
		if (!transitions.isEmpty()) {
			return transitions[0]
		}
		return null
	}

	/**
	 * Calls {@link StateSpace#opFromPredicate(StateId, String, String, Integer)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @param nrOfSolutions to be found
	 * @return a list of solutions found, or an empty list if no solutions were found
	 */
	def List<Transition> findTransitions(String name, List<String> predicates, int nrOfSolutions) {
		String predicate = predicates == []? "TRUE = TRUE" : predicates.join(" & ")
		try {
			def newOps = stateSpace.transitionFromPredicate(this, name, predicate, nrOfSolutions)
			transitions.addAll(newOps)
			return newOps
		} catch(IllegalArgumentException e) {
			// Skip
		}
		return Collections.EMPTY_LIST
	}

	def State anyOperation(filter) {
		List<Transition> ops = getOutTransitions(true)
		if (filter != null && filter instanceof String) {
			ops=ops.findAll {
				it.getName().matches(filter);
			}
		}
		if (filter != null && filter instanceof ArrayList) {
			ops=ops.findAll {
				filter.contains(it.getName())
			}
		}
		if (!ops.isEmpty()) {
			Collections.shuffle(ops)
			def op = ops[0]
			def newState = op.getDestination()
			newState.explore()
			return newState;
		}
		this
	}

	def State anyEvent(filter) {
		anyOperation(filter);
	}

	/**
	 * Takes a formula and evaluates it via the {@link StateId#eval(IEvalElement)}
	 * method. The formula is parsed via the {@link AbstractModel#parseFormula(String)} method.
	 * @param String representation of a formula
	 * @return the {@link IEvalResult} calculated from ProB
	 */
	def AbstractEvalResult eval(String formula) {
		return eval(stateSpace.getModel().parseFormula(formula))
	}

	/**
	 * Takes a formula and evaluateds it via the {@link StateId#eval(List)} method.
	 * @param formula as IEvalElement
	 * @return the {@link IEvalResult} calculated by ProB
	 */
	def AbstractEvalResult eval(IEvalElement formula) {
		eval([formula])[0]
	}

	def List<AbstractEvalResult> eval(IEvalElement... formulas) {
		return eval(formulas as List)
	}

	/**
	 * @param formulas to be evaluated
	 * @return list of results calculated by ProB for a given formula
	 */
	def List<AbstractEvalResult> eval(List<IEvalElement> formulas) {
		def cmds = formulas.findAll {
			!values.containsKey(it)
		}.collect { it.getCommand(this) }
		if (!cmds.isEmpty()) {
			stateSpace.execute(new ComposedCommand(cmds))
		}
		def results = cmds.collectEntries {
			[
				it.getEvalElement(),
				it.getValue()
			]
		}

		formulas.collect {
			values.containsKey(it) ? values.get(it) : results.get(it)
		}
	}



	def String toString() {
		return id;
	}

	def String getId() {
		return id;
	}

	def long numericalId() {
		return id == "root" ? -1 : id as long;
	}

	def String getStateRep() {
		if (stateSpace.getModel().getFormalismType() == FormalismType.B) {
			GetBStateCommand cmd = new GetBStateCommand(this)
			stateSpace.execute(cmd)
			return cmd.getState()
		}
		return StringUtil.generateString("unknown")
	}




	def boolean equals(Object that) {
		if (that instanceof State) {
			return this.id.equals(that.getId()) && this.getStateSpace().equals(that.getStateSpace());
		}
		return false
	}


	def int hashCode() {
		return Objects.hashCode(id, stateSpace)
	};



	def boolean isInitialised() {
		if (!explored) {
			explore()
		}
		return initialised
	}

	def boolean isInvariantOk() {
		if (!explored) {
			explore()
		}
		return invariantOk
	}

	def boolean isMaxTransitionsCalculated() {
		if (!explored) {
			explore()
		}
		return maxTransitionsCalculated
	}

	def boolean isTimeoutOccurred() {
		if (!explored) {
			explore()
		}
		return timeoutOccurred
	}

	def Set<String> getTransitionsWithTimeout() {
		if (!explored) {
			explore()
		}
		return transitionsWithTimeout
	}

	def Collection<StateError> getStateErrors() {
		if (!explored) {
			explore()
		}
		return stateErrors
	}

	/**
	 * If the state has not yet been explored (i.e. the default number
	 * of outgoing transitions has not yet been calculated by ProB), this
	 * is done via the {@link StateId#explore()} method. By default, the list of
	 * {@link OpInfo} objects created will not be evaluated (i.e. certain
	 * information about the transition will be lazily retrieved from ProB
	 * at a later time). However, if an optional parameter is supplied and
	 * set to true, the evaluation of all of the {@link OpInfo} objects will
	 * occur before the list is returned via the {@link StateSpace#evaluateOps(Collection)}
	 * method.
	 * @param evaluate whether or not the list of transitions should
	 * 		be evaluated. By default this is set to false.
	 * @return the outgoing transitions from this state
	 */
	def List<Transition> getOutTransitions(boolean evaluate=false, FormulaExpand expansion=FormulaExpand.truncate) {
		if (!explored) {
			explore()
		}
		if(evaluate) {
			stateSpace.evaluateTransitions(transitions, expansion)
		}
		transitions
	}

	def State explore() {
		ExploreStateCommand cmd = new ExploreStateCommand(stateSpace, id, stateSpace.getSubscribedFormulas());
		stateSpace.execute(cmd);
		transitions = cmd.getNewTransitions()
		values.putAll(cmd.getFormulaResults())
		initialised = cmd.isInitialised()
		invariantOk = cmd.isInvariantOk()
		timeoutOccurred = cmd.isTimeoutOccured()
		maxTransitionsCalculated = cmd.isMaxOperationsReached()
		stateErrors = cmd.getStateErrors()
		transitionsWithTimeout = cmd.getOperationsWithTimeout()
		explored = true
		this
	}

	def Map<IEvalElement, AbstractEvalResult> getValues() {
		Set<IEvalElement> formulas = stateSpace.getSubscribedFormulas();
		def toEvaluate = []
		for (f in formulas) {
			if (!values.containsKey(f)) {
				toEvaluate << f
			}
		}
		if (!toEvaluate.isEmpty()) {
			def cmd = new EvaluateRegisteredFormulasCommand(this.getId(), toEvaluate)
			stateSpace.execute(cmd)
			values.putAll(cmd.getResults())
		}
		values
	}
}
