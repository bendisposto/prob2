package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class Operation {
	private final String id;
	private final String name;
	private final List<String> params = new ArrayList<String>();

	public Operation(final String id, final String name,
			final List<String> params2) {
		this.id = id;
		this.name = name;
		for (String string : params2) {
			params.add(string);
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + "(" + Joiner.on(",").join(params) + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Operation) {
			Operation that = (Operation) obj;
			boolean b = that.id.equals(id);
			return b;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
