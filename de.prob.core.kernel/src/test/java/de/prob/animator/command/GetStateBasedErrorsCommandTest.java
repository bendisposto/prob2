package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.cli.StateError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetStateBasedErrorsCommandTest {


	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand("42");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("get_state_errors", t.getFunctor());
		
		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isNumber());		
		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());

	}	
	
	@Test
	public void testProcessResult() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Errors")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("error",
						new CompoundPrologTerm("foo"), new CompoundPrologTerm("bar"),
						new CompoundPrologTerm("baz"))));

		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand("state");
		command.processResult(map);


		Collection<StateError> coll = command.getResult();
		StateError se = coll.iterator().next();
		assertEquals("foo", se.getEvent());
		assertEquals("bar", se.getShortDescription());
		assertEquals("baz", se.getLongDescription());
		
	}
	
	@Test
	public void testProcessResultEmpty() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Errors")).thenReturn(
				new ListPrologTerm());

		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand("state");
		command.processResult(map);


		Collection<StateError> coll = command.getResult();
		assertEquals(Collections.emptyList(), coll);
	}
	
	@Test(expected = ProBException.class)
	public void testErrorProcessResult1() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Errors")).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("foobar")));

		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand("state");
		command.processResult(map);		
	}
	
	@Test(expected = ProBException.class)
	public void testErrorProcessResult2() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get("Errors")).thenReturn(new CompoundPrologTerm("foobar"));

		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand("state");
		command.processResult(map);		
	}
	
}
