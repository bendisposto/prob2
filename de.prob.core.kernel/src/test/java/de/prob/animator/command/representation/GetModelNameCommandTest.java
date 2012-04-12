package de.prob.animator.command.representation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetModelNameCommandTest {

	@Test
	public void testWriteCommand() throws ProBException {
		StructuredPrologOutput spo = new StructuredPrologOutput();
		GetModelNameCommand gmnc = new GetModelNameCommand();
		gmnc.writeCommand(spo);
		spo.fullstop().flush();
		List<PrologTerm> sentences = spo.getSentences();
		assertFalse(sentences.isEmpty());
		PrologTerm nextPT = sentences.get(0);
		assertNotNull(nextPT);
		assertTrue(nextPT instanceof CompoundPrologTerm);
		assertEquals("get_name", nextPT.getFunctor());
		assertTrue(nextPT.getArgument(1).isVariable());
		assertEquals("Name", nextPT.getArgument(1).getFunctor());
	}

	@Test
	public void testProcessResult() throws ProBException {
		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		when(map.get("Name")).thenReturn(new CompoundPrologTerm("=P"));
		GetModelNameCommand gmnc = new GetModelNameCommand();
		gmnc.processResult(map);
		assertEquals("=P", gmnc.getName());
	}
}
