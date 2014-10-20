package de.prob.statespace

import com.google.common.base.Objects

import de.prob.animator.command.ComposedCommand
import de.prob.animator.command.ExploreStateCommand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.IEvalResult


/**A reference to the state object in the ProB core.
 *
 * Note: This class contains a reference to the StateSpace object to which this state
 * reference belongs. In order for the garbage collector to work correctly, dereference
 * any StateId objects after they are no longer needed.
 *
 * @author joy
 */
class StateId {

	protected def String id;
	def StateSpace stateSpace;
	def boolean explored
	def List<OpInfo> ops = []
	def boolean initialised
	def boolean invariantOk
	def Map<IEvalElement, IEvalResult> values = new HashMap<IEvalElement, IEvalResult>()

	def StateId(String id, StateSpace space) {
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
		OpInfo op = stateSpace.opFromPredicate(this, method, predicate , 1)[0];
		ops << op
		return op.getDestId().explore();
	}

	def StateId perform(String event, String... params) {
		return perform(event, params as List)
	}

	/**
	 * Uses {@link StateSpace#opFromPredicate} to calculate the destination state of the event
	 * with the specified event name and the conjunction of the parameters.
	 * An exception will be thrown if the specified event and params are invalid for this StateId.
	 * @param event String event name to execute
	 * @param params List of String predicates
	 * @return {@link StateId} that results from executing the specified event
	 */
	def StateId perform(String event, List<String> params) {
		def op = findTransition(event, params)
		return op.getDestId().explore();
	}

	def OpInfo findTransition(String name, String... predicates) {
		return findTransition(name, predicates as List)
	}

	def OpInfo findTransition(String name, List<String> predicates) {
		return findTransitions(name, predicates, 1)[0]
	}

	def List<OpInfo> findTransitions(String name, List<String> predicates, int nrOfSolutions) {
		if (name.startsWith("\$") && !(name == "\$setup_constants" || name == "\$initialise_machine")) {
			name = name.substring(1)
		}

		String predicate = predicates == []? "TRUE = TRUE" : predicates.join(" & ")
		def newOps = stateSpace.opFromPredicate(this, name, predicate, nrOfSolutions)
		ops.addAll(newOps)
		return newOps
	}

	/**
	 * Takes a formula and evaluates it via the {@link StateId#eval(IEvalElement)}
	 * method. The formula is parsed via the {@link AbstractModel#parseFormula(String)} method.
	 * @param String representation of a formula
	 * @return the {@link IEvalResult} calculated from ProB
	 */
	def IEvalResult eval(String formula) {
		if (!isInitialised()) {
			return null
		}
		return eval(stateSpace.getModel().parseFormula(formula))
	}

	/**
	 * Takes a formula and evaluateds it via the {@link StateId#eval(List)} method.
	 * @param formula as IEvalElement
	 * @return the {@link IEvalResult} calculated by ProB
	 */
	def IEvalResult eval(IEvalElement formula) {
		if (!isInitialised()) {
			return null
		}
		return eval([formula])[0]
	}

	def List<IEvalResult> eval(IEvalElement... formulas) {
		return eval(formulas as List)
	}

	/**
	 * @param formulas to be evaluated
	 * @return list of results calculated by ProB for a given formula
	 */
	def List<IEvalResult> eval(List<IEvalElement> formulas) {
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
			if (values.containsKey(it)) {
				return values.get(it)
			} else {
				return results.get(it)
			}
		}
	}



	def String toString() {
		return id;
	}

	def String getId() {
		return id;
	};

	def long numericalId() {
		return id == "root" ? -1 : id as long;
	}


	def boolean equals(Object that) {
		if (that instanceof StateId) {
			return this.id.equals(that.getId()) && this.getStateSpace().equals(that.getStateSpace());
		}
		return false
	}


	def int hashCode() {
		return Objects.hashCode(id, stateSpace)
	};

	def StateId anyOperation(filter) {
		List<OpInfo> ops = getOutTransitions(true)
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
		Collections.shuffle(ops)
		def op = ops[0]
		def newState = op.getDestId()
		newState.explore()
		return newState;
	}

	def StateId anyEvent(filter) {
		anyOperation(filter);
	}

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
		return isInvariantOk()
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
	def List<OpInfo> getOutTransitions(boolean evaluate=false) {
		if (!explored) {
			explore()
		}
		if(evaluate) {
			stateSpace.evaluateOps(ops)
		}
		ops
	}

	def StateId explore() {
		ExploreStateCommand cmd = new ExploreStateCommand(stateSpace, id, stateSpace.getSubscribedFormulas());
		stateSpace.execute(cmd);
		ops = cmd.getNewTransitions()
		values.putAll(cmd.getFormulaResults())
		initialised = cmd.isInitialised()
		invariantOk = cmd.isInvariantOk()
		explored = true
		this
	}
}
