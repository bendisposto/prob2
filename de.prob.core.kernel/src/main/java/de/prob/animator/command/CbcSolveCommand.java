package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
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
public class CbcSolveCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(CbcSolveCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final IEvalElement evalElements;
	private final List<EvaluationResult> values = new ArrayList<EvaluationResult>();

	private String result;

	public CbcSolveCommand(final IEvalElement evalElement) {
		this.evalElements = evalElement;
	}

	public List<EvaluationResult> getValues() {
		return values;
	}

	public String getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		PrologTerm prologTerm = bindings.get(EVALUATE_TERM_VARIABLE);

		assert prologTerm instanceof CompoundPrologTerm;

		String functor = prologTerm.getFunctor();

		if ("time_out".equals(functor)) {
			result = "time out";
		}
		if ("contradiction_found".equals(functor)) {
			result = "cannot be solved";
		}
		if ("solution".equals(functor)) {
			ListPrologTerm solutionBindings = BindingGenerator
					.getList(prologTerm.getArgument(1));

			ArrayList<String> comps = new ArrayList<String>();

			for (PrologTerm b : solutionBindings) {
				CompoundPrologTerm t = (CompoundPrologTerm) b;
				comps.add(translate(t));
			}

			result = Joiner.on(", ").join(comps);
		}
		if ("no_solution_found".equals(functor)) {
			result = "no solution found (but there might exist one)";
		}

	}

	private String translate(CompoundPrologTerm t) {
		return t.getArgument(1) + "=" + t.getArgument(3).getFunctor();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("cbc_solve");
		pout.openList();
		evalElements.printProlog(pout);
		pout.closeList();
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

}
