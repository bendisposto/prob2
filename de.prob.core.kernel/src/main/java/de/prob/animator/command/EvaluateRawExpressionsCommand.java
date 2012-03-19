package de.prob.animator.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateRawExpressionsCommand implements ICommand {
	
	Logger logger = LoggerFactory.getLogger(GetInvariantsCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final List<AbstractEvalElement> evalElements;
	private final String stateId;
	private List<String> values;

	public EvaluateRawExpressionsCommand(
			final List<AbstractEvalElement> evalElements, final String id) {
		this.evalElements = evalElements;
		this.stateId = id;
	}

//	public static List<String> evaluate(final Animator animator,
//			final List<AbstractEvalElement> evalElements, final String id) {
//		EvaluateRawExpressionsCommand command = new EvaluateRawExpressionsCommand(
//				evalElements, id);
//		animator.execute(command);
//		return command.getValues();
//	}

	public List<String> getValues() {
		return values;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException{
		try {
			ListPrologTerm prologTerm = BindingGenerator.getList(bindings
					.get(EVALUATE_TERM_VARIABLE));
			values = PrologTerm.atomicStrings(prologTerm);
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}

	}

	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("evaluate_raw_expressions");
		pout.printAtomOrNumber(stateId);
		pout.openList();

		// print parsed expressions/predicates
		for (AbstractEvalElement term : evalElements) {
			final ASTProlog prolog = new ASTProlog(pout, null);
			term.getPrologAst().apply(prolog);
		}
		pout.closeList();
		pout.printVariable(EVALUATE_TERM_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EvaluateRawExpression(");
		boolean first = true;
		for (final AbstractEvalElement term : evalElements) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(term.getLabel());
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
