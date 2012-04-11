package de.prob.model.representation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PredicateTest {

	@Test
	public void test() {
		Predicate a = new Predicate("=D");
		assertFalse(a.equals("I am not a Predicate!"));
		assertEquals("=D", a.getPredicate());
		assertEquals("=D", a.toString());
	}

}
