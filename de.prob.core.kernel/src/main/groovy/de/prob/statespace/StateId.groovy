package de.prob.statespace

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.OpInfo


class StateId {

	def id;
	def StateSpace space;

	def invokeMethod(String method,  params) {
		String predicate;

		if (params == []) predicate = "TRUE = TRUE"
		else predicate = params[0];
		OpInfo op = space.opFromPredicate(this, method,predicate , 1)[0];
		StateId newState = space.getEdgeTarget(op);
		space.explore(newState);
		return newState;
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
		this.space = space;
	}

	def String toString() {
		return id;
	}

	def String getId() {
		return id;
	};

	def boolean equals(Object that) {
		return this.id.equals(that.getId());
	}


	def int hashCode() {
		return id.hashCode()
	};

	def StateId anyOperation(filter) {
		def ops = new ArrayList<OpInfo>()
		ops.addAll(space.outgoingEdgesOf(this));
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
		def ns = space.getEdgeTarget(op)
		space.explore(ns)
		return ns;
	}

	def anyEvent(filter) {
		anyOperation(filter);
	}
}
