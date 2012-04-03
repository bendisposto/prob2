package de.prob.animator.command;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.animator.command.notImplemented.GetMachineObjectsCommand;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetMachineObjectsCommandTest {

	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		GetMachineObjectsCommand command = new GetMachineObjectsCommand();
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("get_classicalb_machine_objects", t.getFunctor());
		
		assertEquals(2, t.getArity());
		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());		

		PrologTerm argument2 = t.getArgument(2);
		assertTrue(argument2.isVariable());
	}

}
