package de.prob.animator.command;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of a formula. Uses the Prolog Parser, not the Java
 * Parser
 * 
 * @author bendisposto
 * 
 */
public class RemoteEvaluateCommand implements ICommand {

	public enum EEvaluationStrategy {
		EXISTENTIAL("evaluate_formula", true), UNIVERSAL("evaluate_tautology",
				false);
		private final String prolog;
		private final boolean existential;

		EEvaluationStrategy(String prolog, boolean existential) {
			this.prolog = prolog;
			this.existential = existential;
		}
	}

	Logger logger = LoggerFactory.getLogger(RemoteEvaluateCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final String formula;
	private EvaluationResult value;

	private final EEvaluationStrategy quantifier;

	public RemoteEvaluateCommand(final String formula,
			EEvaluationStrategy quantifier) {
		this.formula = formula;
		this.quantifier = quantifier;
	}

	public EvaluationResult getValue() {
		return value;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		PrologTerm term = bindings.get(EVALUATE_TERM_VARIABLE);

		if (term instanceof ListPrologTerm) {
			ListPrologTerm lpt = (ListPrologTerm) term;
			ArrayList<String> list = new ArrayList<String>();

			String code = lpt.get(0).getFunctor();

			for (int i = 1; i < lpt.size(); i++) {
				list.add(lpt.get(i).getArgument(1).getFunctor());
			}

			value = new EvaluationResult(code, "", "", Joiner.on(", ").join(
					list), false);
		} else {
			String v = term.getArgument(1).getFunctor();
			String solution = term.getArgument(2).getFunctor();
			value = new EvaluationResult(formula, v, solution, "",
					quantifier.existential);
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(quantifier.prolog);
		pout.printString(formula);
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}
}
