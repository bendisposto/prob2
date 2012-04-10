package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetEnabledOperationsCommandTest {

	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetEnabledOperationsCommand command = new GetEnabledOperationsCommand(
				"id");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("computeOperationsForState", t.getFunctor());

		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isAtom());

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}

	@Test
	public void testProcessResult1() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("PLOps")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("bindings",
						new IntegerPrologTerm(24),
						new CompoundPrologTerm("foo"), new CompoundPrologTerm(
								"root"), new IntegerPrologTerm(27),
						new CompoundPrologTerm("sproing"),
						new CompoundPrologTerm("ding"))));

		GetEnabledOperationsCommand command = new GetEnabledOperationsCommand(
				"state");
		command.processResult(map);

		List<OpInfo> list = command.getEnabledOperations();
		OpInfo io = list.get(0);
		assertEquals("24", io.id);
		assertEquals("foo", io.name);
		assertEquals("root", io.src);
		assertEquals("27", io.dest);
		assertEquals("ding", io.params);

	}

	@Test
	public void testProcessResult2() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("PLOps")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("bindings",
						new IntegerPrologTerm(24),
						new CompoundPrologTerm("foo"),
						new IntegerPrologTerm(50), new IntegerPrologTerm(27),
						new CompoundPrologTerm("sproing"),
						new CompoundPrologTerm("ding"))));

		GetEnabledOperationsCommand command = new GetEnabledOperationsCommand(
				"state");
		command.processResult(map);

		List<OpInfo> list = command.getEnabledOperations();
		OpInfo io = list.get(0);
		assertEquals("24", io.id);
		assertEquals("foo", io.name);
		assertEquals("50", io.src);
		assertEquals("27", io.dest);
		assertEquals("ding", io.params);

	}

}
