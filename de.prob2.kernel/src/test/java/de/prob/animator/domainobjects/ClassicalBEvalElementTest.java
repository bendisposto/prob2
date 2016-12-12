package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassicalBEvalElementTest {

	@Test
	public void testExpression() {
		ClassicalB element = new ClassicalB("9");
		assertEquals("#EXPRESSION", element.getKind());
	}

	@Test(expected = EvaluationException.class)
	public void testExpressionParserError() {
		ClassicalB element = new ClassicalB("9 + ");
		assertEquals("#EXPRESSION", element.getKind());
	}

	@Test
	public void testPredicate() {
		ClassicalB element = new ClassicalB("9:NAT");
		assertEquals("#PREDICATE", element.getKind());
	}

	@Test(expected = EvaluationException.class)
	public void testPredicateParserError() {
		ClassicalB element = new ClassicalB("9:NAT & ");
		assertEquals("#PREDICATE", element.getKind());
	}

}
