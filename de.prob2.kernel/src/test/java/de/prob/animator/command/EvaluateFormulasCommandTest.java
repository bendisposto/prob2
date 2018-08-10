package de.prob.animator.command;

import java.util.Collection;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluateFormulasCommandTest {

	@Test
	public void testWriteCommand() {
		IEvalElement element = new ClassicalB("1<3", FormulaExpand.EXPAND);

		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		EvaluateFormulaCommand command = new EvaluateFormulaCommand(element,
				"root");
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();

		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm t = sentences.iterator().next();
		assertNotNull(t);
		assertTrue(t instanceof CompoundPrologTerm);
		assertEquals("evaluate_formula", t.getFunctor());
		assertEquals(3, t.getArity());
		PrologTerm t1 = t.getArgument(1);
		assertEquals("root", t1.getFunctor());
		PrologTerm t2 = t.getArgument(2);
		assertEquals("eval", t2.getFunctor());
		PrologTerm t3 = t.getArgument(3);
		assertEquals("Res", t3.getFunctor());
	}

	@Test
	public void testProcessResult() {

		IEvalElement element = new ClassicalB("1<3", FormulaExpand.EXPAND);

		final CompoundPrologTerm lpt = mkResult("true");
		ISimplifiedROMap<String, PrologTerm> m1 = key -> lpt;
		EvaluateFormulaCommand command = new EvaluateFormulaCommand(element,
				"root");
		command.processResult(m1);

		AbstractEvalResult value = command.getValue();
		assertEquals("true", ((EvalResult) value).getValue());
		assertEquals("3", ((EvalResult) value).getSolutions().get("a"));
	}

	private static CompoundPrologTerm mkResult(final String r) {
		return new CompoundPrologTerm("result", new CompoundPrologTerm(r),
				new ListPrologTerm(new CompoundPrologTerm("bind",
						new CompoundPrologTerm("a"),
						new CompoundPrologTerm("3"))), new CompoundPrologTerm(
						"foo"));
	}

}
