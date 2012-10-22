package de.prob.animator.command.representation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetInvariantsCommandTest {

	@Test
	public void testWriteCommand() {
		final StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		final GetInvariantsCommand command = new GetInvariantsCommand(
				new NodeIdAssignment());
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		final Collection<PrologTerm> sentences = prologTermOutput
				.getSentences();
		final PrologTerm next = sentences.iterator().next();

		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		final CompoundPrologTerm t = (CompoundPrologTerm) next;

		assertEquals("get_invariants", t.getFunctor());
		assertEquals(1, t.getArity());
		final PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isVariable());
	}

	@Ignore
	@Test
	public void testProcessResult() {
		@SuppressWarnings("unchecked")
		final ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		final ListPrologTerm lpt = new ListPrologTerm(new CompoundPrologTerm(
				"inv1", new CompoundPrologTerm("active /\\ waiting"),
				new IntegerPrologTerm(1)), new CompoundPrologTerm("inv2",
				new CompoundPrologTerm("active /\\ ready"),
				new IntegerPrologTerm(2)));
		when(map.get("LIST")).thenReturn(lpt);

		final NodeIdAssignment nia = mock(NodeIdAssignment.class);
		final Node mockedNode = mock(Node.class);
		when(nia.lookupById(anyInt())).thenReturn(mockedNode);
		// when(mockedNode.apply(any(DepthFirstAdapter.class)));
		// TODO fix test to test for parse errors
		final GetInvariantsCommand command = new GetInvariantsCommand(nia);
		command.processResult(map);

		final List<ClassicalB> invariants = command.getInvariants();
		assertEquals("active /\\ waiting", invariants.get(0).getCode());
		assertEquals("active /\\ ready", invariants.get(1).getCode());
	}
}
