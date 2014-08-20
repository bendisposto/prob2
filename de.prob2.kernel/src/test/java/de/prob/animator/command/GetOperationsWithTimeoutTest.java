package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetOperationsWithTimeoutTest {

	@Test
	public void testProcessResults() {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("TO")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("foobar"),
						new CompoundPrologTerm("bliblablub")));

		GetOperationsWithTimeout command = new GetOperationsWithTimeout("state");
		command.processResult(map);

		List<String> list = command.getTimeouts();
		assertEquals("foobar", list.get(0));
		assertEquals("bliblablub", list.get(1));
	}

	@Test(expected = ResultParserException.class)
	public void testErrorProcessResults() {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		when(map.get(anyString()))
				.thenReturn(new CompoundPrologTerm("bang!!!"));
		GetOperationsWithTimeout command = new GetOperationsWithTimeout("state");
		command.processResult(map);
	}

	@Test
	public void testWriteCommand() {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetOperationsWithTimeout command = new GetOperationsWithTimeout("state");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("op_timeout_occurred", t.getFunctor());

		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isAtom());
		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());

	}

}
