package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;

public class ClassicalBEvalElementTest {

	@Test
	public void testExpression() throws BException {
		ClassicalBEvalElement element = new ClassicalBEvalElement("9");
		assertEquals(EXPRESSION, element.getType());
	}

	@Test
	public void testPredicate() throws BException {
		ClassicalBEvalElement element = new ClassicalBEvalElement("9:NAT");
		assertEquals(PREDICATE, element.getType());
	}

}
