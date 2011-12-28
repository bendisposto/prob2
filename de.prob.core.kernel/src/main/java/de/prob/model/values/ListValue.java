package de.prob.model.values;

class ListValue extends AbstractValue {

	private final AbstractValue[] values;

	public ListValue(AbstractValue... values) {
		this.values = values;
	}

	@Override
	public void prettyprint(StringBuilder sb) {
		sb.append("{");
		for (int i = 0; i < values.length - 1; i++) {
			values[i].prettyprint(sb);
			sb.append(",");
		}
		values[values.length - 1].prettyprint(sb);
		sb.append("}");
	}
}