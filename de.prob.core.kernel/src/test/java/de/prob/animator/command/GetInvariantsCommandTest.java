package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommandTest {

	@Test(expected = ProBException.class)
	public void testErrorProcessResults() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		when(map.get(anyString()))
				.thenReturn(new CompoundPrologTerm("bang!!!"));
		GetInvariantsCommand command = new GetInvariantsCommand();
		command.processResult(map);
	}

	@Test
	public void testProcessResults() throws ProBException {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("CONJUNCT")).thenReturn(new CompoundPrologTerm("foobar"));
		when(map.get("LIST")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("foobaz"),
						new CompoundPrologTerm("dada")));

		GetInvariantsCommand command = new GetInvariantsCommand();
		command.processResult(map);

		String conj = command.getInvariant();
		assertEquals("foobar", conj);

		List<String> list = command.getInvariantAsList();
		assertEquals("foobaz", list.get(0));
		assertEquals("dada", list.get(1));

	}
	
	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetInvariantsCommand command = new GetInvariantsCommand();
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("get_invariants", t.getFunctor());
		
		// why is arity 2? could iterate even further?
		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		// is it variable?
		assertTrue(argument.isVariable());
		// second argument is?
		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}
}
