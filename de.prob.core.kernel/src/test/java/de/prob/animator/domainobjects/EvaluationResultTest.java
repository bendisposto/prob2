package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EvaluationResultTest {

	@Test
	public void testToString() {
		EvaluationResult a = new EvaluationResult("0", "c1", "yay", "", "",
				"exists", new String[0], false);
		assertEquals("0: yay", a.toString());
		EvaluationResult b = new EvaluationResult("1", "c2", "the", "is 4", "",
				"exists", new String[0], false);
		assertEquals("1: the Solution: is 4", b.toString());
		EvaluationResult c = new EvaluationResult("3", "c3", "", "",
				"does not work", "exists", new String[0], false);
		assertEquals("'Errors: does not work'", c.toString());
	}

}
