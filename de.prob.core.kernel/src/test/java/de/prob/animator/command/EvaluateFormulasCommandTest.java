package de.prob.animator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateFormulasCommandTest {

	@Test
	public void testWriteCommand() throws Exception {

		List<IEvalElement> evalElements = new ArrayList<IEvalElement>();
		evalElements.add(new ClassicalB("1<3"));
		evalElements.add(new ClassicalB("3"));

		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		EvaluateFormulasCommand command = new EvaluateFormulasCommand(
				evalElements, "root");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();

		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm t = sentences.iterator().next();
		assertNotNull(t);
		assertTrue(t instanceof CompoundPrologTerm);
		assertEquals("evaluate_formulas", t.getFunctor());
		assertEquals(3, t.getArity());
		PrologTerm t1 = t.getArgument(1);
		assertEquals("root", t1.getFunctor());
		PrologTerm t2 = t.getArgument(2);
		assertTrue(t2 instanceof ListPrologTerm);
		PrologTerm t3 = t.getArgument(3);
		assertEquals("Val", t3.getFunctor());
	}

	@Test
	public void testProcessResult() throws Exception {
		List<IEvalElement> evalElements = new ArrayList<IEvalElement>();
		evalElements.add(new ClassicalB("1<3"));
		evalElements.add(new ClassicalB("3"));
		evalElements.add(new ClassicalB("1>3"));
		evalElements.add(new ClassicalB("99"));

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ListPrologTerm lpt = new ListPrologTerm(mk_result("true"),
				mk_result("false"), mk_result("true"), mk_result("false"));
		when(map.get("Val")).thenReturn(lpt);

		EvaluateFormulasCommand command = new EvaluateFormulasCommand(
				evalElements, "root");
		command.processResult(map);

		List<IEvalResult> vals = command.getValues();

		assertEquals(vals.size(), 4);

		assertEquals(((EvalResult) vals.get(0)).getValue(), "true");
		assertEquals(((EvalResult) vals.get(1)).getValue(), "false");
		assertEquals(((EvalResult) vals.get(2)).getValue(), "true");
		assertEquals(((EvalResult) vals.get(3)).getValue(), "false");
	}

	private CompoundPrologTerm mk_result(final String r) {
		return new CompoundPrologTerm("result", new CompoundPrologTerm(r),
				new ListPrologTerm(new CompoundPrologTerm("bind",
						new CompoundPrologTerm("a"), new CompoundPrologTerm(
								"int", new IntegerPrologTerm(3)),
						new CompoundPrologTerm("3"))), new CompoundPrologTerm(
						"foo"));
	}

}
