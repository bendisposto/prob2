package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetOperationByPredicateCommandTest {

	@Test
	public void testWriteCommand() throws Exception {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		ClassicalBEvalElement pred = new ClassicalBEvalElement("1<3");
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				"blA", "bLa", pred, 23);
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("execute_custom_operations", t.getFunctor());
		assertEquals(6, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isAtom());
		PrologTerm arg2 = t.getArgument(2);
		assertTrue(arg2.isAtom());
		PrologTerm arg3 = t.getArgument(3);
		assertTrue(arg3 instanceof CompoundPrologTerm);
		assertEquals("less", arg3.getFunctor());
		PrologTerm arg4 = t.getArgument(4);
		assertTrue(arg4.isNumber());
		PrologTerm arg5 = t.getArgument(5);
		assertTrue(arg5.isVariable());
		PrologTerm arg6 = t.getArgument(6);
		assertTrue(arg6.isVariable());
	}

	@Test
	public void testProcessResults() throws ProBException {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		ListPrologTerm lpt = new ListPrologTerm(new CompoundPrologTerm("term",
				new CompoundPrologTerm("blaH"), new CompoundPrologTerm("blAh"),
				new CompoundPrologTerm("bLah"), new CompoundPrologTerm("Blah"),
				new CompoundPrologTerm("blah"), new ListPrologTerm(
						new CompoundPrologTerm("BLAH"))));

		when(map.get(anyString())).thenReturn(lpt);

		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				null, null, null, 0);
		command.processResult(map);

		List<OpInfo> ops = command.getOperations();
		assertEquals(1, ops.size());
		OpInfo element = ops.get(0);
		assertEquals("blaH", element.id);
		assertEquals("blAh", element.name);
		assertEquals("bLah", element.src);
		assertEquals("Blah", element.dest);
		assertTrue(element.params.contains("BLAH"));

	}

}
