package de.prob.model.values;

class PairValue extends AbstractValue {

	private final AbstractValue left;
	private final AbstractValue right;

	public PairValue(AbstractValue left, AbstractValue right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void prettyprint(StringBuilder sb) {
		left.prettyprint(sb);
		sb.append("|->");
		right.prettyprint(sb);
	}
}