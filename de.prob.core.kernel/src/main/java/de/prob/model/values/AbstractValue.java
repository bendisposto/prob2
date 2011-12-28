package de.prob.model.values;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AbstractValue {
	abstract void prettyprint(StringBuilder sb);

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.prettyprint(sb);
		return sb.toString();
	}
}