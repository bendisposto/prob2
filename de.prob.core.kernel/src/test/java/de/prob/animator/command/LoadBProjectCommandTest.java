package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import org.junit.Test;
import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class LoadBProjectCommandTest {

	@Test
	public void testWriteCommand() throws ProBException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("examples/scheduler.mch");
		File f = null;
		try {
			f = new File(resource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		LoadBProjectCommand command = new LoadBProjectCommand(f);
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("load_classical_b", t.getFunctor());
		assertEquals(2, t.getArity());

		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isList());

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}

	@Test(expected = ProBException.class)
	public void testProcessResults() throws ProBException {

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		when(map.get(anyString())).thenReturn(
				new ListPrologTerm(new CompoundPrologTerm("blah blah blah")));

		LoadBProjectCommand command = new LoadBProjectCommand(null);
		command.processResult(map);
	}

	@Test
	public void testProcessResult2() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);

		ListPrologTerm l = new ListPrologTerm();
		when(map.get(anyString())).thenReturn(l);

		LoadBProjectCommand command = new LoadBProjectCommand(null);
		command.processResult(map);
	}
}
