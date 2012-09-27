package de.prob.cli;

import static org.junit.Assert.*;

import org.junit.Test;

import test.AbstractUnitTest;

public class PortPatternTest extends AbstractUnitTest {

	@Test
	public void testSuccess1() {
		String line = "Port: 3422";
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertTrue("Pattern does not match", matches);
		assertEquals(Integer.valueOf(3422), pattern.getValue());
	}

	@Test
	public void testSuccess2() {
		String line = " s \t  Port: 3422";
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertTrue("Pattern does not match", matches);
		assertEquals(Integer.valueOf(3422), pattern.getValue());
	}

	@Test
	public void testFailingMatch1()  {
		String line = "Port: ";
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern matches, but should not", matches);
	}

	@Test
	public void testEmptyInput()  {
		String line = "";
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern matches empty string", matches);
	}

	@Test
	public void testNullInput()  {
		String line = null;
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern matches null input", matches);
	}

	@Test
	public void testTrailingChars()  {
		String line = "    Port: 3422 ";
		PortPattern pattern = new PortPattern();
		boolean matches = pattern.matchesLine(line);
		assertFalse("Pattern does not match", matches);
	}

}
