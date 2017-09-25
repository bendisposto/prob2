package de.prob.animator.domainobjects;

import org.junit.Test;

import static org.junit.Assert.*;

public class TLAEvalElementTest {

	@Test
	public void testExpression() {
		TLA element = new TLA("9");
		assertEquals(EvalElementType.EXPRESSION, element.getKind());
	}

	@Test
	public void testPredicate() {
		TLA element = new TLA("9 \\in Int");
		assertEquals(EvalElementType.PREDICATE, element.getKind());
	}

}
