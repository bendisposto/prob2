package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of Classical-B Predicates and Expressions.
 * 
 * @author joy
 * 
 */
public class EvaluateFormulasCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "evaluate_formulas";

	Logger logger = LoggerFactory.getLogger(EvaluateFormulasCommand.class);

	private static final String EVALUATE_RESULT_VARIABLE = "Res";

	private final List<IEvalElement> evalElements;
	private final List<AbstractEvalResult> values = new ArrayList<>();

	private String stateId;

	public EvaluateFormulasCommand(final List<IEvalElement> evalElements, final String stateId) {
		this.evalElements = evalElements;
		this.stateId = stateId;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {

		ListPrologTerm terms = BindingGenerator.getList(bindings, EVALUATE_RESULT_VARIABLE);
		for (PrologTerm term : terms) {
			values.add(EvalResult.getEvalResult(term));
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.printAtomOrNumber(stateId);

		pout.openList();
		for (IEvalElement evalElement : evalElements) {
			printEvalTerm(pout, evalElement);
		}
		pout.closeList();

		pout.printVariable(EVALUATE_RESULT_VARIABLE);
		pout.closeTerm();
	}

	private void printEvalTerm(final IPrologTermOutput pout, IEvalElement evalElement) {
		if (evalElement instanceof ProBEvalElement) {
			pout.openTerm("eval_typed");
			evalElement.printProlog(pout);
			pout.printAtom(evalElement.expansion().name());
		} else {
			pout.openTerm("eval");
			evalElement.printProlog(pout);
			pout.printAtom(evalElement.getKind());
			pout.printAtom(evalElement.getCode());
			pout.printAtom(evalElement.expansion().name());
		}
		pout.closeTerm();
	}

	public List<AbstractEvalResult> getValues() {
		return Collections.unmodifiableList(values);
	}

}
