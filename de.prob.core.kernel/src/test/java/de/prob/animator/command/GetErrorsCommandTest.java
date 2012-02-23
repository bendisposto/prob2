package de.prob.animator.command;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetErrorsCommandTest {

	@Test
	public void testWriteCommand() {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetErrorsCommand command = new GetErrorsCommand();
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("getErrorMessages", t.getFunctor());
		assertEquals(1, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());
	}

	@Test
	public void testProcessResults() {
		ISimplifiedROMap<String, PrologTerm> map = new ISimplifiedROMap<String, PrologTerm>() {
			@Override
			public PrologTerm get(final String arg0) {
				return new ListPrologTerm(new CompoundPrologTerm("foobar"));

			}
		};

		GetErrorsCommand command = new GetErrorsCommand();
		command.processResult(map);

		List<String> errors = command.getErrors();
		assertEquals("foobar", errors.get(0));

	}
}
