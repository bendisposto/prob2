package de.prob.model.values;

class AtomValue extends AbstractValue {
	private final String value;

	public AtomValue(String value) {
		this.value = value;
	}

	public void prettyprint(StringBuilder sb) {
		sb.append(value);
	}
}