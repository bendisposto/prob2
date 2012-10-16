package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;

public class ClassicalBEvalElementTest {

	@Test
	public void testExpression() throws BException {
		ClassicalB element = new ClassicalB("9");
		assertEquals("#EXPRESSION", element.getKind());
	}

	@Test
	public void testPredicate() throws BException {
		ClassicalB element = new ClassicalB("9:NAT");
		assertEquals("#PREDICATE", element.getKind());
	}

}
