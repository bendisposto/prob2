package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

public class TLAEvalElementTest {

	@Test
	public void testExpression() {
		TLA element = new TLA("9");
		assertEquals("#EXPRESSION", element.getKind());
	}

	@Test
	public void testPredicate() {
		TLA element = new TLA("9 \\in Int");
		assertEquals("#PREDICATE", element.getKind());
	}

}
