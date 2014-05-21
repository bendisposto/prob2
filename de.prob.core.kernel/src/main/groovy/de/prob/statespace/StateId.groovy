package de.prob.statespace

import de.prob.animator.domainobjects.CSP
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EventB
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.model.representation.CSPModel
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


	def value(String key) {
		def v = space.valuesAt(this);
		for (def entry : v.entrySet()) {
			if(entry.getKey().code == key) {
				def res = entry.getValue()
				if (res.hasProperty("value")) {
					return res.getValue()
				}
				return res
			}
		}
		def m = space.getModel();
		if(m instanceof ClassicalBModel) {
			return space.eval(this, [key as ClassicalB]).get(0).value
		}
		if(m instanceof EventBModel) {
			return space.eval(this, [key as EventB]).get(0).value
		}
		if(m instanceof CSPModel) {
			return space.eval(this, [key as CSP]).get(0).value
		}
	}


	def eval(formula) {
		def f = formula;
		if (!(formula instanceof IEvalElement)) {
			f = formula as ClassicalB;
		}
		space.eval(this, [f])[0]
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
