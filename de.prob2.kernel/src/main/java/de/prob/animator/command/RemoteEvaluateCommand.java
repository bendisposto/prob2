package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of a formula. Uses the Prolog Parser, not the Java
 * Parser
 * 
 * @author bendisposto
 * 
 */
public class RemoteEvaluateCommand extends AbstractCommand {

	public enum EEvaluationStrategy {
		EXISTENTIAL("evalb_evaluate_formula", true), UNIVERSAL("evalb_evaluate_tautology",
				false);
		private final String prolog;
		private final boolean existential;

		EEvaluationStrategy(final String prolog, final boolean existential) {
			this.prolog = prolog;
			this.existential = existential;
		}

		public boolean isExistential() {
			return existential;
		}

	}

	Logger logger = LoggerFactory.getLogger(RemoteEvaluateCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final String formula;
	private AbstractEvalResult result;
	List<String> atomicStrings;
	private boolean enumerationWarnings;
	private String resultType;

	private final EEvaluationStrategy quantifier;

	public RemoteEvaluateCommand(final String formula,
			final EEvaluationStrategy quantifier) {
		this.formula = formula;
		this.quantifier = quantifier;
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

			result = new ComputationNotCompletedResult(code, Joiner.on(", ").join(list));
			//		new EvaluationResult("", code, "", "", Joiner.on(", ")
			//		.join(list), "error", new ArrayList<String>(), false);
			atomicStrings = Collections.emptyList();
			enumerationWarnings = false;
			resultType = "error";
		} else {
			String v = term.getArgument(1).getFunctor();
			ListPrologTerm sols = BindingGenerator.getList(term.getArgument(2));
			Map<String,String> solutions = sols.isEmpty() ? Collections.<String,String>emptyMap() : new HashMap<String, String>();
			for (PrologTerm pt : sols) {
				CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(pt, 2);
				solutions.put(cpt.getArgument(1).getFunctor(), cpt.getArgument(2).getFunctor());
			}
			result = new EvalResult(v, solutions);			
			
			resultType = term.getArgument(3).getFunctor();
			atomicStrings = ListPrologTerm
					.atomicStrings((ListPrologTerm) term.getArgument(4));
			enumerationWarnings = "true".equals(term.getArgument(5)
					.getFunctor());
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(quantifier.prolog);
		pout.printString(formula);
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}
	
	public AbstractEvalResult getResult() {
		return result;
	}
	
	public List<String> getAtomicStrings() {
		return atomicStrings;
	}
	
	public boolean hasEnumerationWarnings() {
		return enumerationWarnings;
	}
	
	public String getResultType() {
		return resultType;
	}
	
	public String getFormula() {
		return formula;
	}
}
