package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;

public class TLAEvalElementTest {

	@Test
	public void testExpression() throws BException {
		TLA element = new TLA("9");
		assertEquals("#EXPRESSION", element.getKind());
	}
	
	@Test
	public void testPredicate() throws BException {
		TLA element = new TLA("9 \\in Int");
		assertEquals("#PREDICATE", element.getKind());
	}

}
