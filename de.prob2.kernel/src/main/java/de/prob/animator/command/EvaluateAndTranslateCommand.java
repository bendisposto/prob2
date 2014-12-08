package de.prob.animator.command;

import de.prob.animator.domainobjects.TranslateFormula;
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class EvaluateAndTranslateCommand extends EvaluationCommand {

	private static final String EVALUATE_RESULT_VARIABLE = "Res";

	public EvaluateAndTranslateCommand(final TranslateFormula formula,
			final String id) {
		super(formula, id);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluate_and_process_prolog_values");
		pto.printAtomOrNumber(stateId);
		pto.openTerm("eval");
		evalElement.printProlog(pto);
		pto.printAtom(evalElement.getKind());
		pto.printAtom(evalElement.getCode());
		pto.closeTerm();
		pto.printVariable(EVALUATE_RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		value = TranslatedEvalResult.getResult(bindings
				.get(EVALUATE_RESULT_VARIABLE));
	}

}
