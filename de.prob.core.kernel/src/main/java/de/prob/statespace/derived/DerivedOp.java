package de.prob.statespace.derived;

import de.prob.statespace.OpInfo;

public class DerivedOp extends OpInfo {

	public DerivedOp(final String id, final String src, final String dest,
			final String label) {
		super(id, src, dest);
		rep = label;
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
