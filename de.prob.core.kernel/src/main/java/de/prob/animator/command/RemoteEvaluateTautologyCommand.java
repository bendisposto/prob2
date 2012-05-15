package de.prob.animator.command;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.ProBException;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the values of a tautology. Uses the Prolog Parser, not the Java Parser 
 * 
 * @author bendisposto
 * 
 */
public class RemoteEvaluateTautologyCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(RemoteEvaluateTautologyCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final String tautology;
	private EvaluationResult value;

	public RemoteEvaluateTautologyCommand(final String tautology) {
		this.tautology = tautology;
	}

	public EvaluationResult getValue() {
		return value;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {

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
			value = new EvaluationResult(tautology, v, solution, "",
					false);
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) throws ProBException {
		pout.openTerm("evaluate_tautology");
		pout.printString(tautology);
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}
}
