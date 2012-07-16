package de.prob.statespace

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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

	def getProperty(String property){
		return space.info.getVariable(this, property);
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
}
