package de.prob.statespace;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StateId {
	private final String id;
	private final String hash;

	public StateId(final String id, final String state) {
		this.id = id;
		this.hash = hash(state);
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof StateId) {
			StateId that = (StateId) obj;
			return that.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		// if (hash == null)
		return id.hashCode();
	}

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
