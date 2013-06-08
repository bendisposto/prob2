package de.prob.statespace

import de.prob.animator.domainobjects.CSP
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EventB
import de.prob.animator.domainobjects.IEvalElement
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.EventBModel
import de.prob.scripting.CSPModel
import de.prob.statespace.derived.AbstractDerivedStateSpace


class StateId {

	protected def id;
	def StateSpace space;

	//FIXME delete
	@Deprecated
	def invokeMethod(String method,  params) {
		return perform(method,params)
	}

	def perform(String method,  params) {
		String predicate;
		if (params == []) predicate = "TRUE = TRUE"
		else predicate = params[0];
		OpInfo op = space.opFromPredicate(this, method,predicate , 1)[0];
		StateId newState = space.getDest(op);
		space.explore(newState);
		return newState;
	}


	def value(String key) {
		def v = space.valuesAt(this);
		for (def entry : v.entrySet()) {
			if(entry.getKey().code == key) {
				return entry.getValue().value
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
