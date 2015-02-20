package de.prob.animator.domainobjects;

import java.util.HashMap;

/**
 * @author joy
 * 
 *         Provides uniform methods for {@link IEvalElement}s. Ensures that the
 *         {@link #equals(Object)} and {@link #hashCode()} methods are correctly
 *         implemented (using the value of {@link #code}) so that
 *         {@link HashMap}s work correctly with {@link IEvalElement}s extending
 *         this class.
 */
public abstract class AbstractEvalElement implements IEvalElement {
	protected String code;

	@Override
	public String getCode() {
		return code;
	}
}
