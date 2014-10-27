package de.prob.animator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateFormulasCommandTest {

	@Test
	public void testWriteCommand() throws Exception {
		IEvalElement element = new ClassicalB("1<3");

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
	public void testProcessResult() throws Exception {

		IEvalElement element = new ClassicalB("1<3");

		final CompoundPrologTerm lpt = mk_result("true");
		ISimplifiedROMap<String, PrologTerm> m1 = new ISimplifiedROMap<String, PrologTerm>() {

			@Override
			public PrologTerm get(final String key) {
				return lpt;
			}

		};
		EvaluateFormulaCommand command = new EvaluateFormulaCommand(element,
				"root");
		command.processResult(m1);

		IEvalResult value = command.getValue();
		assertEquals(((EvalResult) value).getValue(), "true");
		assertEquals(((EvalResult) value).getSolutions().get("a"), "3");
	}

	private CompoundPrologTerm mk_result(final String r) {
		return new CompoundPrologTerm("result", new CompoundPrologTerm(r),
				new ListPrologTerm(new CompoundPrologTerm("bind",
						new CompoundPrologTerm("a"),
						new CompoundPrologTerm("3"))), new CompoundPrologTerm(
						"foo"));
	}

}
