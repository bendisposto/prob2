package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.ValueTranslator;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of Classical-B Predicates and Expressions.
 * 
 * @author joy
 * 
 */
public class EvaluateFormulaCommand extends EvaluationCommand {

	Logger logger = LoggerFactory.getLogger(EvaluateFormulaCommand.class);

	private static final String EVALUATE_RESULT_VARIABLE = "Res";

	public EvaluateFormulaCommand(final IEvalElement evalElement,
			final String id) {
		super(evalElement, id);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		PrologTerm term = bindings.get(EVALUATE_RESULT_VARIABLE);

		if (term instanceof ListPrologTerm) {
			ListPrologTerm lpt = (ListPrologTerm) term;
			ArrayList<String> list = new ArrayList<String>();

			String code = lpt.get(0).getFunctor();

			for (int i = 1; i < lpt.size(); i++) {
				list.add(lpt.get(i).getArgument(1).getFunctor());
			}

			value = new ComputationNotCompletedResult(code, Joiner.on(',')
					.join(list));
		} else {
			String value_str = term.getArgument(1).getFunctor();
			Map<String, String> solutions = new HashMap<String, String>();
			Map<String, PrologTerm> solutionsSource = new HashMap<String, PrologTerm>();
			ListPrologTerm list = BindingGenerator.getList(term.getArgument(2));
			for (PrologTerm t : list) {
				CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(t, 2);
				solutions.put(cpt.getArgument(1).getFunctor(),
						new ValueTranslator().toGroovy(cpt.getArgument(2))
								.toString());
				solutionsSource.put(cpt.getArgument(1).getFunctor(),
						cpt.getArgument(2));
			}
			String code = term.getArgument(3).getFunctor();
			value = new EvalResult(code, value_str, solutions, solutionsSource);
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("evaluate_formula");
		pout.printAtomOrNumber(stateId);

		pout.openTerm("eval");
		evalElement.printProlog(pout);
		pout.printAtom(evalElement.getKind().toString());
		pout.printAtom(evalElement.getCode());
		pout.closeTerm();

		pout.printVariable(EVALUATE_RESULT_VARIABLE);
		pout.closeTerm();
	}

}
