package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvaluationResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateRegisteredFormulasCommand extends AbstractCommand {
	private final String RESULTS = "Results";
	private final String stateId;
	private final Map<IEvalElement, IEvaluationResult> results = new HashMap<IEvalElement, IEvaluationResult>();
	private final List<IEvalElement> formulas;

	public EvaluateRegisteredFormulasCommand(final String stateId,
			final Collection<IEvalElement> formulas) {
		this.stateId = stateId;
		this.formulas = new ArrayList<IEvalElement>(formulas);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("eval_registered_formulas");
		pto.printAtomOrNumber(stateId);
		pto.openList();
		for (IEvalElement formula : formulas) {
			pto.printAtom(formula.getFormulaId().uuid);
		}
		pto.closeList();
		pto.printVariable(RESULTS);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm terms = bindings.get(RESULTS);
		if (terms instanceof ListPrologTerm) {
			ListPrologTerm lpt = BindingGenerator.getList(terms);
			for (PrologTerm term : lpt) {
				if (term instanceof ListPrologTerm) {
					ListPrologTerm listP = (ListPrologTerm) term;
					ArrayList<String> list = new ArrayList<String>();

					String code = listP.get(0).getFunctor();

					for (int i = 1; i < listP.size(); i++) {
						list.add(listP.get(i).getArgument(1).getFunctor());
					}

					results.put(formulas.get(lpt.indexOf(term)),
							new EvaluationResult(stateId, code, "", "", Joiner
									.on(", ").join(list), "exists",
									new ArrayList<String>(), false));
				} else {
					String value = term.getArgument(1).getFunctor();
					String solution = term.getArgument(2).getFunctor();
					String code = term.getArgument(3).getFunctor();
					results.put(formulas.get(lpt.indexOf(term)),
							new EvaluationResult(stateId, code, value,
									solution, "", "exists",
									new ArrayList<String>(), false));
				}

			}
		}
	}

	public Map<IEvalElement, IEvaluationResult> getResults() {
		return results;
	}

	public String getStateId() {
		return stateId;
	}
}
