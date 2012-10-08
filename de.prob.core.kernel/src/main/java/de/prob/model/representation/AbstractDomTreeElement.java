package de.prob.model.representation;

import java.util.List;

public abstract class AbstractDomTreeElement {
	static int count = 0;
	public final String uuid = "Formula_" + (++count);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractDomTreeElement) {
			AbstractDomTreeElement that = (AbstractDomTreeElement) obj;
			return this.uuid.equals(that.uuid);
		}
		return false;
	}

	@Override
	public String toString() {
		return uuid;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	abstract public String getLabel();

	abstract public List<AbstractDomTreeElement> getSubcomponents();

	abstract public boolean toEvaluate();

}
