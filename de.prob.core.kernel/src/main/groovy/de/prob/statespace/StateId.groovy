package de.prob.statespace

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class StateId {

	def id;
	def hash;
	def StateSpaceInfo space;

	def invokeMethod(String method, Object params) {
		println "MOPHandler was asked to invoke ${method}"
		if(params != null){
			params.each{ println "\twith parameter ${it}" }
		}
	}

	def getProperty(String property){
		return space.getVariable(this, property);
	}

	def StateId(id, hash,  info) {
		this.id = id;
		this.hash = hash;
		this.space = info;
	}

	def String toString() {
		return id + " " + hash;
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
