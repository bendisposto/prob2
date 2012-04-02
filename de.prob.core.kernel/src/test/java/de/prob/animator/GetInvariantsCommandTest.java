package de.prob.animator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import de.prob.ProBException;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.cli.ProBInstance;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommandTest {

	String cliAnswer = "yes('.'(=('LIST','.'(inv('ready /\\ waiting = {}',46),'.'(inv('active /\\ (ready \\/ waiting) = {}',51),'.'(inv('card(active) <= 1',58),'.'(inv('active = {} => waiting = {}',62),[]))))),[]))";

	@Ignore
	@Test(expected = ProBException.class)
	public void testErrorProcessResults() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		when(map.get(anyString()))
				.thenReturn(new CompoundPrologTerm("bang!!!"));
		// GetInvariantsCommand command = new GetInvariantsCommand();
		// command.processResult(map);
	}

	@Ignore
	@Test
	public void testProcessResult() throws Exception {

		CommandProcessor processor = new CommandProcessor();
		GetErrorsCommand getErrors = mock(GetErrorsCommand.class);
		ProBInstance cli = mock(ProBInstance.class);

		when(cli.send(startsWith("get_invariants"))).thenReturn(cliAnswer);
		when(cli.send(startsWith("getErr"))).thenReturn(
				"yes('.'(=('Errors',[]),[])).");

		Logger logger = mock(Logger.class);
		processor.configure(cli, logger);

		AnimatorImpl a = new AnimatorImpl(cli, processor, getErrors);
		// GetInvariantsCommand command = new GetInvariantsCommand();
		// a.execute(command);
		// List<StringWithLocation> invariant = command.getInvariant();
		// for (StringWithLocation s : invariant) {
		// System.out.println(s);
		// }

	}

	// FIXME fix the test
	// @Test
	// public void testProcessResults() throws ProBException {
	//
	// @SuppressWarnings("unchecked")
	// ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
	//
	// when(map.get("CONJUNCT")).thenReturn(new CompoundPrologTerm("foobar"));
	// when(map.get("LIST")).thenReturn(
	// new ListPrologTerm(new CompoundPrologTerm("foobaz"),
	// new CompoundPrologTerm("dada")));
	//
	// GetInvariantsCommand command = new GetInvariantsCommand();
	// command.processResult(map);
	//
	// String conj = command.getInvariant();
	// assertEquals("foobar", conj);
	//
	// List<String> list = command.getInvariantAsList();
	// assertEquals("foobaz", list.get(0));
	// assertEquals("dada", list.get(1));
	//
	// }

	@Test
	@Ignore
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		// GetInvariantsCommand command = new GetInvariantsCommand();
		// command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();

		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;

		assertEquals("get_invariants", t.getFunctor());
		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());
		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}
}
