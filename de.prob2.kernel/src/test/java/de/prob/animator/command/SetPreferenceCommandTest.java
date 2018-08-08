package de.prob.animator.command;

import java.util.Collection;

import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.junit.Test;

import static org.junit.Assert.*;

public class SetPreferenceCommandTest {

	@Test
	public void testWriteCommand() {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		SetPreferenceCommand command = new SetPreferenceCommand("foo", "bar");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();

		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm t = sentences.iterator().next();

		assertNotNull(t);
		assertTrue(t instanceof CompoundPrologTerm);
		assertEquals("set_eclipse_preference", t.getFunctor());
		assertEquals(2, t.getArity());

		PrologTerm argument1 = t.getArgument(1);
		assertTrue(argument1.isAtom());
		assertEquals("foo", argument1.toString());

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isAtom());
		assertEquals("bar", argument2.toString());
	}

}
