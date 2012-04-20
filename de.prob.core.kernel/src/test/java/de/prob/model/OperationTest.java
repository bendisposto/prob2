package de.prob.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.prob.statespace.Operation;

public class OperationTest {

	@Test
	public void test() {
		Operation a = new Operation("I", "HEART", "LIFE!!!");
		Operation b = new Operation("I", "HEART", "LIFE!!!");
		Operation c = new Operation("U", "<3", "LIFE!!!");

		assertTrue(a.equals(b));
		assertFalse(a.equals(c));
		assertFalse(a.equals("I'm not an Operation!"));
		assertEquals("I", a.getId());
		assertEquals("HEART", a.getName());
		assertEquals("HEART(LIFE!!!)", a.toString());
		assertEquals(73, a.hashCode());
	}

}
