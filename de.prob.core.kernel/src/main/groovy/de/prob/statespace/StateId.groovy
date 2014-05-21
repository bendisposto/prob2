package de.prob.statespace

import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.IEvalResult
import de.prob.statespace.derived.AbstractDerivedStateSpace


class StateId {

	protected def id;
	def StateSpace space;

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
		return perform(method, params)
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
		if(event.startsWith("\$") && !(event == "\$setup_constants" || event == "\$initialise_machine")) {
			event = event.substring(1)
		}

		String predicate = params == []? "TRUE = TRUE" : params.join(" & ")
		OpInfo op = space.opFromPredicate(this, event, predicate , 1)[0];
		StateId newState = space.getDest(op);
		space.explore(newState);
		return newState;
	}


	/**
	 * Evaluates the given formula key via the {@link #eval(Object)} method.
	 * If the result has a "value" property, this is returned.
	 * Otherwise, the result is returned.
	 *
	 * This method is meant to be used for extracting the value of variables at a given state.
	 * For more complicated formulas, we suggest using the {@link #eval(Object)} method.
	 *
	 * @param key String representation of the formula
	 * @return the value property of the result, if one exists, or the result object itself
	 */
	def value(String key) {
		IEvalResult res = eval(key)
		if (res.hasProperty("value")) {
			return res.getValue()
		}
		return res
	}


	/**
	 * Takes a formula and evaluates it via the {@link StateSpace#eval(StateId, java.util.List)}
	 * method. If the input is a String, the formula is parsed via the {@link AbstractModel#parseFormula(String)} method.
	 * @param formula String or IEvalElement representation of a formula
	 * @return the {@link IEvalResult} calculated from ProB
	 */
	def IEvalResult eval(formula) {
		def f = formula;
		if (!(formula instanceof IEvalElement)) {
			f = space.getModel().parseFormula(f)
		}
		return space.eval(this, [f])[0]
	}

	def StateId(id, space) {
		this.id = id;
		if(space instanceof StateSpace) {
			this.space = space;
		} else if(space instanceof AbstractDerivedStateSpace) {
			this.space = ((AbstractDerivedStateSpace) space).getStateSpace()
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
		return this.id.equals(that.getId());
	}


	def int hashCode() {
		return id.hashCode()
	};

	def StateId anyOperation(filter) {
		def ops = new ArrayList<OpInfo>()
		ops.addAll(space.getOutEdges(this));
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
		def op = ops.get(0)
		def ns = space.getDest(op)
		space.explore(ns)
		return ns;
	}

	def anyEvent(filter) {
		anyOperation(filter);
	}
}
