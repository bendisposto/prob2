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
				new HashMap<String, String>(), null);
		assertEquals("yay", a.toString());
		EvalResult b = new EvalResult("c1", "yay", solutions, null);
		assertTrue(b.toString().startsWith("yay: "));
		ComputationNotCompletedResult c = new ComputationNotCompletedResult(
				"c3", "does not work");
		assertEquals("does not work", c.toString());
	}

}
