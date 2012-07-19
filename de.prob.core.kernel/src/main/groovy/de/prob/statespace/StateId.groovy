package de.prob.statespace

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections.SingletonList

import de.prob.animator.domainobjects.IEvalElement

class StateId {

	def id;
	def hash;
	def StateSpace space;


	def invokeMethod(String method, Object params) {
		String predicate = params[0];
		Operation op = space.opFromPredicate(this, method,predicate , 1)[0];
		OperationId opid = new OperationId(op.getId());
		StateId newState = space.getEdgeTarget(opid);
		space.explore(newState);
		return newState;
	}

	def getProperty(String property) {
		def result = space.info.getVariable(this, property);
		if (result == null) {
			def evalElement = space.getForms().get(property)
			if (evalElement == null)
				throw NoSuchElementException("Missing attribute "+property);

			def known = space.info.variables.get(this)
			if (known.keySet().contains(evalElement.getCode())) {
				return known.get(evalElement.getCode())
			}

			result = space.eval(getId(), new SingletonList<IEvalElement>(evalElement))
		}
		return result;
	}

	def StateId(id, hash, space) {
		this.id = id;
		this.hash = hash;
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
		return this.hash.hashCode()
	};


	public String hash(final String vars) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			final String str = new String(md.digest(vars.getBytes()));
			return str;
		} catch (NoSuchAlgorithmException e) {
			return vars;
		}
	}

	public String getHash() {
		return hash;
	}


	def StateId anyOperation(filter) {
		def spaceInfo = space.info
		def ops = new ArrayList()
		ops.addAll(space.outgoingEdgesOf(this));
		if (filter != null && filter instanceof String) {
			ops=ops.findAll {
				def opinfo = spaceInfo.getOp(it)
				def name = opinfo.getName()
				name.matches(filter);
			}
		}
		if (filter != null && filter instanceof ArrayList) {
			ops=ops.findAll {
				def opinfo = spaceInfo.getOp(it)
				def name = opinfo.getName()
				filter.contains(name)
			}
		}
		Collections.shuffle(ops)
		def op = ops.get(0)
		def ns = space.getEdgeTarget(op)
		space.explore(ns)
		return ns;
	}

	def anyEvent = this.&anyOperation;
}
