package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

public class EvaluationResultTest {

	@Test
	public void testToString() {
		EvaluationResult a = new EvaluationResult("yay", "", "");
		assertEquals("yay", a.toString());
		EvaluationResult b = new EvaluationResult("the", "is 4", "");
		assertEquals("the Solution: is 4", b.toString());
		EvaluationResult c = new EvaluationResult("", "", "does not work");
		assertEquals("'Errors: does not work'", c.toString());
	}

}
