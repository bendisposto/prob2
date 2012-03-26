package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetStateValuesCommandTest {

	@Test
	public void testProcessResult() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Bindings")).thenReturn(
				new ListPrologTerm(
					new CompoundPrologTerm("binding",
						new CompoundPrologTerm("foo"), new CompoundPrologTerm("bar"),
						new CompoundPrologTerm("hummelbummel")),
					new CompoundPrologTerm("binding",
						new CompoundPrologTerm("bli"), new CompoundPrologTerm("bla"),
						new CompoundPrologTerm("blub"))
				));

		GetStateValuesCommand command = new GetStateValuesCommand("stateID");
		command.processResult(map);

		HashMap<String, String> hmap = command.getResult();
		assertEquals("hummelbummel", hmap.get("foo"));
		assertEquals("blub", hmap.get("bli"));
	}
	
	@Test(expected = ProBException.class)
	public void testErrorProcessResult1() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Bindings")).thenReturn(new CompoundPrologTerm("foo"));

		GetStateValuesCommand command = new GetStateValuesCommand("stateID");
		command.processResult(map);
	}

	
	@Test(expected = ProBException.class)
	public void testErrorProcessResult2() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Bindings")).thenReturn(
				new ListPrologTerm(
					new CompoundPrologTerm("scampi",
						new CompoundPrologTerm("foo"), new CompoundPrologTerm("bar"),
						new CompoundPrologTerm("hummelbummel"))
				));
		
		GetStateValuesCommand command = new GetStateValuesCommand("stateID");
		command.processResult(map);
	}
	
	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetStateValuesCommand command = new GetStateValuesCommand("stateID");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("getStateValues", t.getFunctor());
		
		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isAtom());

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}
}
