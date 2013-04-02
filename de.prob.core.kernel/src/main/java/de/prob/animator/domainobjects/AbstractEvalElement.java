package de.prob.animator.domainobjects;


public abstract class AbstractEvalElement implements IEvalElement {

	protected String code;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IEvalElement) {
			IEvalElement that = (IEvalElement) obj;
			return that.getCode().equals(getCode());
		}
		return false;
	}

}
