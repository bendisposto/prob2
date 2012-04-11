package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.Start;

public class ClassicalBEvalElementTest {

	@Test
	public void testExpression() throws BException {
		ClassicalBEvalElement element = new ClassicalBEvalElement("9");
		Start start = element.parse();
		assertTrue(start.getPParseUnit() instanceof AExpressionParseUnit);
	}

	@Test
	public void testPredicate() throws BException {
		ClassicalBEvalElement element = new ClassicalBEvalElement("9:NAT");
		Start start = element.parse();
		assertTrue(start.getPParseUnit() instanceof APredicateParseUnit);
	}

}
