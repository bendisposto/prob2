package de.prob.animator.command.notImplemented;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * This command sends the ID of an expression to the ProB core and receives the
 * corresponding label (usually the pretty-printed expression) and the IDs of
 * the expression's child nodes.
 * 
 * @see EvaluationGetTopLevelCommand
 * @see EvaluationGetValuesCommand
 * @author plagge
 */
public class EvaluationExpandCommand implements ICommand {
	private static final String LABEL_VARNAME = "Lbl";
	private static final String CHILDREN_VARNAME = "Chs";

	private final PrologTerm evaluationElement;
	
	Logger logger = LoggerFactory.getLogger(EvaluationExpandCommand.class);

	private String label;
	private List<PrologTerm> children;

	public EvaluationExpandCommand(final PrologTerm evaluationElement) {
		this.evaluationElement = evaluationElement;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException{
		try {
			label = BindingGenerator.getCompoundTerm(bindings.get(LABEL_VARNAME), 0).getFunctor();
			children = BindingGenerator.getList(bindings.get(CHILDREN_VARNAME));
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
		
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_expand_formula");
		evaluationElement.toTermOutput(pto);
		pto.printVariable(LABEL_VARNAME);
		pto.printVariable(CHILDREN_VARNAME);
		pto.closeTerm();
	}

	public String getLabel() {
		return label;
	}

	public List<PrologTerm> getChildrenIds() {
		return children;
	}
}
