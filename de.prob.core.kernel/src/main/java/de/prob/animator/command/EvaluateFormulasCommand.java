package de.prob.animator.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
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
public class EvaluateFormulasCommand extends AbstractCommand {

	Logger logger = LoggerFactory.getLogger(EvaluateFormulasCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final List<IEvalElement> evalElements;
	private final String stateId;
	private final List<IEvalResult> values = new ArrayList<IEvalResult>();

	public EvaluateFormulasCommand(final List<IEvalElement> evalElements,
			final String id) {
		this.evalElements = evalElements;
		stateId = id;
	}

	public List<IEvalElement> getFormulas() {
		return evalElements;
	}

	public List<IEvalResult> getValues() {
		return values;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		ListPrologTerm prologTerm = BindingGenerator.getList(bindings
				.get(EVALUATE_TERM_VARIABLE));

		for (PrologTerm term : prologTerm) {
			if (term instanceof ListPrologTerm) {
				ListPrologTerm lpt = (ListPrologTerm) term;
				ArrayList<String> list = new ArrayList<String>();

				String code = lpt.get(0).getFunctor();

				for (int i = 1; i < lpt.size(); i++) {
					list.add(lpt.get(i).getArgument(1).getFunctor());
				}

				values.add(new ComputationNotCompletedResult(code, Joiner.on(
						',').join(list)));
			} else {
				String value = term.getArgument(1).getFunctor();
				Map<String, String> solutions = new HashMap<String, String>();
				Map<String, PrologTerm> solutionsSource = new HashMap<String, PrologTerm>();
				ListPrologTerm list = BindingGenerator.getList(term
						.getArgument(2));
				for (PrologTerm t : list) {
					CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(
							t, 3);
					solutions.put(cpt.getArgument(1).getFunctor(), cpt
							.getArgument(3).getFunctor());
					solutionsSource.put(cpt.getArgument(1).getFunctor(),
							cpt.getArgument(2));
				}
				String code = term.getArgument(3).getFunctor();
				values.add(new EvalResult(code, value, solutions,
						solutionsSource));
			}
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("evaluate_formulas");
		pout.printAtomOrNumber(stateId);
		pout.openList();

		// print parsed expressions/predicates
		for (IEvalElement term : evalElements) {
			pout.openTerm("eval");
			term.printProlog(pout);
			pout.printAtom(term.getKind().toString());
			pout.printAtom(term.getCode());
			pout.closeTerm();
		}
		pout.closeList();
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

	public String getStateId() {
		return stateId;
	}
}
