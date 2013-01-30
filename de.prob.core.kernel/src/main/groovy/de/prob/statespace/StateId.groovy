package de.prob.statespace

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.IEvalElement
import de.prob.animator.domainobjects.OpInfo


class StateId {

	def id;
	def hash;
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

	def StateId(id, vars, space) {
		this.id = id;
		this.hash = getHash(vars);
		this.space = space;
	}

	def String toString() {
		return id;
	}


	def String getId() {
		return id;
	};

	def boolean equals(Object that) {
		return this.hash.equals(that.getHash());
	}


	def int hashCode() {
		return hash.hashCode()
	};


	def String getHash(final String vars) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(vars.getBytes());
			def x = new BigInteger(1, md.digest()).toString(16).padLeft( 40, '0' )
			return x
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace()
			return vars;
		}
	}

	public String getHash() {
		return hash;
	}


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
