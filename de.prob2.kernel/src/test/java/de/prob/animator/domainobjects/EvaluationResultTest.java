package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EvaluationResultTest {

	@Test
	public void testToString() {
		Map<String, String> solutions = new HashMap<String, String>();
		solutions.put("x", "5");
		solutions.put("y", "{1,2,3}");
		EvalResult a = new EvalResult("c1", "yay",
				new HashMap<String, String>());
		assertEquals("yay", a.toString());
		EvalResult b = new EvalResult("c1", "yay", solutions);
		assertTrue(b.toString().equals("yay (x = 5 \u2227 y = {1,2,3})")
				|| b.toString().equals("yay (y = {1,2,3} \u2227 x = 5)"));
		ComputationNotCompletedResult c = new ComputationNotCompletedResult(
				"c3", "does not work");
		assertEquals("does not work", c.toString());
	}

}
