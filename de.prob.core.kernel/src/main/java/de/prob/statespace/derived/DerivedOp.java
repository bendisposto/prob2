package de.prob.statespace.derived;

import de.prob.statespace.OpInfo;

public class DerivedOp extends OpInfo {

	private int count;

	public DerivedOp(final String id, final String src, final String dest,
			final String label, final String count) {
		super(id, src, dest);
		rep = label;
		this.count = Integer.parseInt(count);

	}

	public int getCount() {
		return count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	@Override
	public int hashCode() {
		return src.hashCode() + dest.hashCode() + rep.hashCode();
	}

	@Override
	public boolean equals(final Object that) {
		if (that instanceof DerivedOp) {
			boolean s = this.getSrc().equals(((DerivedOp) that).getSrc());
			boolean d = this.getDest().equals(((DerivedOp) that).getDest());
			boolean r = this.getRep().equals(((DerivedOp) that).getRep());
			return s && d && r;
		}
		return false;
	}
}
