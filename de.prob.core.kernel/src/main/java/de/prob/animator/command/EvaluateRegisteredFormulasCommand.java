package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.ValueTranslator;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateRegisteredFormulasCommand extends AbstractCommand {
	private final String RESULTS = "Results";
	private final String stateId;
	private final Map<IEvalElement, IEvalResult> results = new HashMap<IEvalElement, IEvalResult>();
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

					results.put(
							formulas.get(lpt.indexOf(term)),
							new ComputationNotCompletedResult(code, Joiner.on(
									", ").join(list)));
				} else {
					String value = term.getArgument(1).getFunctor();
					Map<String, String> solutions = new HashMap<String, String>();
					Map<String, PrologTerm> solutionsSource = new HashMap<String, PrologTerm>();

					ListPrologTerm list = BindingGenerator.getList(term
							.getArgument(2));
					for (PrologTerm t : list) {
						CompoundPrologTerm cpt = BindingGenerator
								.getCompoundTerm(t, 2);
						solutions.put(
								t.getArgument(1).getFunctor(),
								new ValueTranslator().toGroovy(
										cpt.getArgument(2)).toString());
						solutionsSource.put(t.getArgument(1).getFunctor(),
								t.getArgument(2));
					}
					String code = term.getArgument(3).getFunctor();
					results.put(formulas.get(lpt.indexOf(term)),
							new EvalResult(code, value, solutions,
									solutionsSource));
				}

			}
		}
	}

	public Map<IEvalElement, IEvalResult> getResults() {
		return results;
	}

	public String getStateId() {
		return stateId;
	}
}
