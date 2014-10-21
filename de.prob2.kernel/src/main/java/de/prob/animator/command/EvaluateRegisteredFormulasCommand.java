package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.SimpleEvalResult;
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
				results.put(formulas.get(lpt.indexOf(term)),
						SimpleEvalResult.getEvalResult(term));
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
