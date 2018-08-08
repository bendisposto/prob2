package de.prob.animator.command;

import de.prob.TestHelper;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CheckBooleanPropertyCommandTest {

	@Test
	public void testWriteCommand() {
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"A_DINGO_ATE_MY_BABY", "root");
		IPrologTermOutput pto = mock(IPrologTermOutput.class);
		cmd.writeCommand(pto);
		verify(pto).openTerm("state_property");
		verify(pto).printVariable(anyString());
		verify(pto).printAtomOrNumber("root");
		verify(pto).printAtom("A_DINGO_ATE_MY_BABY");
	}

	@Test
	public void testProcessResultTrue() {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "true");

		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertTrue(cmd.getResult());
	}

	@Test
	public void testProcessResultFalse() {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "false");
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertFalse(cmd.getResult());
	}

	@Test(expected = ResultParserException.class)
	public void testProcessIllegalResult() {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "fff");
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessResultNull() {
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.getResult();
	}
}
