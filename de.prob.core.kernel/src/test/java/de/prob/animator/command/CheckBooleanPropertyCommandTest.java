package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
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
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(new CompoundPrologTerm("true"));

		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertTrue(cmd.getResult());
	}

	@Test
	public void testProcessResultFalse() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(new CompoundPrologTerm("false"));

		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		cmd.processResult(map);
		assertFalse(cmd.getResult());
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessResultNull() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(new CompoundPrologTerm("no"));

		CheckBooleanPropertyCommand cmd = new CheckBooleanPropertyCommand(
				"BLAH_BLAH", "root");
		try {
			cmd.processResult(map);
		} catch (ProBException e) {

		}
		cmd.getResult();
	}
}
