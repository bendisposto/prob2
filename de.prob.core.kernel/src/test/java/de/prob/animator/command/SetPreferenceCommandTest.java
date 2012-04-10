package de.prob.animator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

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
		assertEquals(argument1.toString(), "foo");

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isAtom());
		assertEquals(argument2.toString(), "bar");
	}

}
