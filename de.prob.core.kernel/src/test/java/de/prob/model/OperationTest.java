package de.prob.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.prob.statespace.Operation;

public class OperationTest {

	@Test
	public void test() {
		ArrayList<String> life = new ArrayList<String>();
		life.add("LIFE");
		life.add("!!!");
		Operation a = new Operation("I", "HEART", life);
		Operation b = new Operation("I", "HEART", life);
		Operation c = new Operation("U", "<3", life);

		assertTrue(a.equals(b));
		assertFalse(a.equals(c));
		assertFalse(a.equals("I'm not an Operation!"));
		assertEquals("I", a.getId());
		assertEquals("HEART", a.getName());
		assertEquals("HEART(LIFE,!!!)", a.toString());
		assertEquals(73, a.hashCode());
	}
}
