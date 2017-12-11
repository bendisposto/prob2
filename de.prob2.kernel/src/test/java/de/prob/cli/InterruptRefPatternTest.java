package de.prob.cli;

import org.junit.Test;

import static org.junit.Assert.*;

public class InterruptRefPatternTest {

	@Test
	public void testSuccess1() {
		String line = "user interrupt reference id: 3422";
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertTrue("Pattern does not match", matches);
		assertEquals(Long.valueOf(3422), pattern.getValue());
	}

	@Test
	public void testSuccess2() {
		String line = "   \t   s  user interrupt reference id: 3422";
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertTrue("Pattern does not match", matches);
		assertEquals(Long.valueOf(3422), pattern.getValue());
	}

	@Test
	public void testFailingMatch1() {
		String line = "user interrupt reference id:      \t";
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern does not match", matches);
	}

	@Test
	public void testEmptyInput() {
		String line = "";
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern does not match", matches);
	}

	@Test
	public void testNullInput() {
		String line = null;
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern does not match", matches);
	}

	@Test
	public void testTrailingChars() {
		String line = "    Port: 3422 ";
		InterruptRefPattern pattern = new InterruptRefPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern does not match", matches);
	}

}
