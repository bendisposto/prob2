package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.ProBException;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of Classical-B Predicates and Expressions.
 * 
 * @author joy
 * 
 */
public class EvaluateFormulasCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(EvaluateFormulasCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final List<ClassicalBEvalElement> evalElements;
	private final String stateId;
	private final List<EvaluationResult> values = new ArrayList<EvaluationResult>();

	// FIXME: Why does this command need access to the id?
	public EvaluateFormulasCommand(
			final List<ClassicalBEvalElement> evalElements, final String id) {
		this.evalElements = evalElements;
		this.stateId = id;
	}

	public List<EvaluationResult> getValues() {
		return values;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		try {

			ListPrologTerm prologTerm = BindingGenerator.getList(bindings
					.get(EVALUATE_TERM_VARIABLE));

			for (PrologTerm term : prologTerm) {
				if (term instanceof ListPrologTerm) {
					ListPrologTerm lpt = (ListPrologTerm) term;
					ArrayList<String> list = new ArrayList<String>();

					String code = lpt.get(1).getFunctor();

					for (int i = 2; i <= lpt.size(); i++) {
						list.add(lpt.get(i).getArgument(1).getFunctor());
					}

					values.add(new EvaluationResult(code, "", "", Joiner.on(
							", ").join(list)));
				} else {
					String value = term.getArgument(1).getFunctor();
					String solution = term.getArgument(2).getFunctor();
					String code = term.getArgument(3).getFunctor();
					values.add(new EvaluationResult(code, value, solution, ""));
				}
			}

		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) throws ProBException {
		pout.openTerm("evaluate_formulas");
		pout.printAtomOrNumber(stateId);
		pout.openList();

		// print parsed expressions/predicates
		for (ClassicalBEvalElement term : evalElements) {
			pout.openTerm("eval");
			final ASTProlog prolog = new ASTProlog(pout, null);
			term.getAst().apply(prolog);
			pout.printAtom(term.getType().toString());
			pout.printAtom(term.getCode());
			pout.closeTerm();
		}
		pout.closeList();
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}
}
