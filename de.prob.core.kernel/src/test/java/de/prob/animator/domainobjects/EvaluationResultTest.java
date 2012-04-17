package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

public class EvaluationResultTest {

	@Test
	public void testToString() {
		EvaluationResult a = new EvaluationResult("c1", "yay", "", "");
		assertEquals("yay", a.toString());
		EvaluationResult b = new EvaluationResult("c2", "the", "is 4", "");
		assertEquals("the Solution: is 4", b.toString());
		EvaluationResult c = new EvaluationResult("c3", "", "", "does not work");
		assertEquals("'Errors: does not work'", c.toString());
	}

}
