package de.prob.animator.command.internal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import test.TestHelper;
import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

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
	public void testProcessResultTrue() throws ProBException {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "true");

		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertTrue(cmd.getResult());
	}

	@Test
	public void testProcessResultFalse() throws ProBException {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "false");
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertFalse(cmd.getResult());
	}

	@Test(expected = ProBException.class)
	public void testProcessIllegalResult() throws ProBException {
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "fff");
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessResultNull() throws ProBException {
		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.getResult();
	}
}
