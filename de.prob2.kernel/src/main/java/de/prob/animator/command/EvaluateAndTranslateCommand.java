package de.prob.animator.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.prob.animator.domainobjects.TranslateFormula;
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.animator.domainobjects.ValueTranslator;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
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
		pto.closeTerm();
		pto.printVariable(EVALUATE_RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm prologTerm = BindingGenerator.getCompoundTerm(
				bindings.get(EVALUATE_RESULT_VARIABLE), 2);
		PrologTerm v = prologTerm.getArgument(1);
		ValueTranslator translator = new ValueTranslator();
		Object vobj = translator.toGroovy(v);

		ListPrologTerm list = BindingGenerator.getList(prologTerm
				.getArgument(2));
		Map<String, Object> solutions = Collections.emptyMap();
		if (!list.isEmpty()) {
			solutions = new HashMap<String, Object>();
		}
		for (PrologTerm pt : list) {
			CompoundPrologTerm sol = BindingGenerator.getCompoundTerm(pt, 2);
			solutions.put(sol.getArgument(1).getFunctor(),
					translator.toGroovy(sol.getArgument(2)));
		}
		value = new TranslatedEvalResult(vobj, solutions);
	}

}
