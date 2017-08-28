package de.prob.animator.domainobjects;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClassicalBEvalElementTest {

	@Test
	public void testExpression() {
		ClassicalB element = new ClassicalB("9");
		assertEquals(EvalElementType.EXPRESSION, element.getKind());
	}

	@Test(expected = EvaluationException.class)
	public void testExpressionParserError() {
		ClassicalB element = new ClassicalB("9 + ");
		assertEquals(EvalElementType.EXPRESSION, element.getKind());
	}

	@Test
	public void testPredicate() {
		ClassicalB element = new ClassicalB("9:NAT");
		assertEquals(EvalElementType.PREDICATE, element.getKind());
	}

	@Test(expected = EvaluationException.class)
	public void testPredicateParserError() {
		ClassicalB element = new ClassicalB("9:NAT & ");
		assertEquals(EvalElementType.PREDICATE, element.getKind());
	}

}
