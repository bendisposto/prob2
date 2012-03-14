package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommandTest {

	@Test(expected = ProBException.class)
	public void testErrorProcessResults() throws ProBException {
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
}
