package de.prob.statespace.derived;

import de.prob.statespace.OpInfo;

public class DerivedOp extends OpInfo {

	private final String color;
	private final String style;

	public DerivedOp(final String id, final String src, final String dest,
			final String label, final String color, final String style) {
		super(id, src, dest);
		rep = label;
		if (color.equals("blue")) {
			this.color = "cornflowerblue";
		} else if (color.equals("black")) {
			this.color = "#666";
		} else {
			this.color = color;
		}
		this.style = style;
	}

	public String getColor() {
		return color;
	}

	public String getStyle() {
		return style;
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
