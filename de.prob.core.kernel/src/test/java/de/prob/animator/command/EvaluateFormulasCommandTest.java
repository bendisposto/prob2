package de.prob.animator.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.EvalElementType;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateFormulasCommandTest {

	@Test
	public void testWriteCommand() throws ProBException {

		List<ClassicalBEvalElement> evalElements = new ArrayList<ClassicalBEvalElement>();
		evalElements.add(new ClassicalBEvalElement("1<3",
				EvalElementType.PREDICATE));
		evalElements.add(new ClassicalBEvalElement("3",
				EvalElementType.EXPRESSION));

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
	public void testProcessResult() throws ProBException {
		List<ClassicalBEvalElement> evalElements = new ArrayList<ClassicalBEvalElement>();
		evalElements.add(new ClassicalBEvalElement("1<3",
				EvalElementType.PREDICATE));
		evalElements.add(new ClassicalBEvalElement("3",
				EvalElementType.EXPRESSION));
		evalElements.add(new ClassicalBEvalElement("1>3"));
		evalElements.add(new ClassicalBEvalElement("99"));

		@SuppressWarnings("unchecked")
		ISimplifiedROMap<String, PrologTerm> map = mock(ISimplifiedROMap.class);
		ListPrologTerm lpt = new ListPrologTerm(mk_result("true"),
				mk_result("false"), mk_result("true"), mk_result("false"));
		when(map.get("Val")).thenReturn(lpt);

		EvaluateFormulasCommand command = new EvaluateFormulasCommand(
				evalElements, "root");
		command.processResult(map);

		List<EvaluationResult> vals = command.getValues();

		assertEquals(vals.size(), 4);

		assertEquals(vals.get(0).value, "true");
		assertEquals(vals.get(1).value, "false");
		assertEquals(vals.get(2).value, "true");
		assertEquals(vals.get(3).value, "false");
	}

	private CompoundPrologTerm mk_result(final String r) {
		return new CompoundPrologTerm("result", new CompoundPrologTerm(r),
				new CompoundPrologTerm(""));
	}

}
