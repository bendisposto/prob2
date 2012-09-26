package de.prob.animator.command.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Test;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetOperationParameterNamesCommandTest {

	@Test
	public void testProcessResult()  {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Names")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("Hupf"),
						new CompoundPrologTerm("klack")));

		GetOperationParameterNamesCommand command = new GetOperationParameterNamesCommand(
				"name");

		command.processResult(map);
		assertEquals(command.getParameterNames().get(0), "Hupf");
		assertEquals(command.getParameterNames().get(1), "klack");
	}

	@Test
	public void testWriteCommand()  {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetOperationParameterNamesCommand command = new GetOperationParameterNamesCommand(
				"name");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("getOperationParameterNames", t.getFunctor());

		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isAtom());
		assertEquals(argument.getFunctor(), "name");

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}

}
