package de.prob.animator.domainobjects;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluationResultTest {

	@Test
	public void testToString() {
		Map<String, String> solutions = new HashMap<>();
		solutions.put("x", "5");
		solutions.put("y", "{1,2,3}");
		EvalResult a = new EvalResult("yay", new HashMap<>());
		assertEquals("yay", a.toString());
		EvalResult b = new EvalResult("yay", solutions);
		assertTrue("yay (x = 5 ∧ y = {1,2,3})".equals(b.toString())
				|| "yay (y = {1,2,3} ∧ x = 5)".equals(b.toString()));
		ComputationNotCompletedResult c = new ComputationNotCompletedResult(
				"c3", "does not work");
		assertEquals("does not work", c.getReason());
	}

}
