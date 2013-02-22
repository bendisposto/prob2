package de.prob.animator.domainobjects;

/**
 * B formulas have either the type PREDICATE or EXPRESSION.
 * 
 * @author joy
 * 
 */
public enum EvalElementType {
	PREDICATE, EXPRESSION;

	@Override
	public String toString() {
		return "#" + super.toString();
	}

}
