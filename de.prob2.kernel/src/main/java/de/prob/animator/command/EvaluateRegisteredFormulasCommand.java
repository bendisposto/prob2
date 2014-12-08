package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.TranslateFormula;
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
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
		pto.openTerm("evaluate_registered_formulas");
		pto.printAtomOrNumber(stateId);
		pto.openList();
		for (IEvalElement formula : formulas) {
			formula.getFormulaId().printUUID(pto);
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
			for (int i = 0; i < lpt.size(); i++) {
				PrologTerm pt = lpt.get(i);
				IEvalElement key = formulas.get(i);
				if (key instanceof TranslateFormula) {
					results.put(key, TranslatedEvalResult.getResult(pt));
				} else {
					results.put(key, EvalResult.getEvalResult(pt));
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
