package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EvaluationResultTest {

	@Test
	public void test() {
		EvaluationResult a = new EvaluationResult("yay", "");
		assertEquals("yay", a.toString());
		EvaluationResult b = new EvaluationResult("the", "is 4");
		assertEquals("the Solution: is 4", b.toString());
	}

}
