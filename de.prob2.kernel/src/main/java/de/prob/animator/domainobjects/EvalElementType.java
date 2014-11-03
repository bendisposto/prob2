package de.prob.animator.domainobjects;

import de.prob.util.StringUtil;

/**
 * B formulas have either the type PREDICATE or EXPRESSION.
 * 
 * @author joy
 * 
 */
public enum EvalElementType {
	PREDICATE, EXPRESSION, ASSIGNMENT;

	@Override
	public String toString() {
		return StringUtil.generateString("#" + super.toString());
	}

}
