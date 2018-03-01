package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates the values of Classical-B Predicates and Expressions.
 * 
 * @author joy
 * 
 */
public class CbcSolveCommand extends AbstractCommand {
	public enum Solvers {
		PROB, KODKOD, SMT_SUPPORTED_INTERPRETER, Z3
	}

	private static final String PROLOG_COMMAND_NAME = "cbc_solve";

	private static final int BINDINGS = 1;

	private static final int VAR_NAME = 1;
	@SuppressWarnings("unused")
	private static final int PROLOG_REP = 2;
	private static final int PRETTY_PRINT = 3;

	Logger logger = LoggerFactory.getLogger(CbcSolveCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private static final String IDENTIFIER_LIST = "IdList";
	private final IEvalElement evalElement;
	private AbstractEvalResult result;
	private final List<String> freeVariables = new ArrayList<>();

	private Solvers solver;

	public CbcSolveCommand(final IEvalElement evalElement) {
		this(evalElement, Solvers.PROB);
	}

	public CbcSolveCommand(final IEvalElement evalElement, final Solvers solver) {
		this.evalElement = evalElement;
		this.solver = solver;
	}

	public AbstractEvalResult getValue() {
		return result;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm idList = bindings.get(IDENTIFIER_LIST);
		if (idList instanceof ListPrologTerm) {
			for (PrologTerm id : (ListPrologTerm) idList) {
				freeVariables.add(id.getFunctor());
			}
		}

		PrologTerm prologTerm = bindings.get(EVALUATE_TERM_VARIABLE);

		assert prologTerm instanceof CompoundPrologTerm;

		String functor = prologTerm.getFunctor();

		if ("time_out".equals(functor)) {
			result = new ComputationNotCompletedResult(evalElement.getCode(), "time out");
		}
		if ("contradiction_found".equals(functor)) {
			result = EvalResult.FALSE;
		}
		if ("solution".equals(functor)) {
			ListPrologTerm solutionBindings = BindingGenerator.getList(prologTerm.getArgument(BINDINGS));

			if (solutionBindings.isEmpty()) {
				result = EvalResult.TRUE;
				return;
			}

			Map<String, String> solutions = new HashMap<>();

			for (PrologTerm b : solutionBindings) {
				CompoundPrologTerm t = (CompoundPrologTerm) b;
				solutions.put(t.getArgument(VAR_NAME).getFunctor(), t.getArgument(PRETTY_PRINT).getFunctor());
			}

			result = new EvalResult("TRUE", solutions);
		}
		if ("no_solution_found".equals(functor)) {
			result = new ComputationNotCompletedResult(evalElement.getCode(),
					"no solution found (but one might exist)");
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.printAtom(solver.toString());
		evalElement.printProlog(pout);
		pout.printVariable(IDENTIFIER_LIST);
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

	public List<String> getFreeVariables() {
		return freeVariables;
	}
}
