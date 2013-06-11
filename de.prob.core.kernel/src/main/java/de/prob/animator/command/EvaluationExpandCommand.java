package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import de.prob.exception.ProBLoggerFactory;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
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
public class EvaluationExpandCommand extends AbstractCommand {
	private static final String LABEL_VARNAME = "Lbl";
	private static final String CHILDREN_VARNAME = "Chs";

	Logger logger = ProBLoggerFactory.getLogger(EvaluationExpandCommand.class);

	private String label;
	private final List<String> childrenIds = new ArrayList<String>();
	private final String id;

	public EvaluationExpandCommand(final String id) {
		this.id = id;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		label = BindingGenerator
				.getCompoundTerm(bindings.get(LABEL_VARNAME), 0).getFunctor();
		ListPrologTerm list = BindingGenerator.getList(bindings
				.get(CHILDREN_VARNAME));
		for (PrologTerm prologTerm : list) {
			childrenIds.add(prologTerm.getFunctor());
		}

	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_expand_formula");
		pto.printAtomOrNumber(id);
		pto.printVariable(LABEL_VARNAME);
		pto.printVariable(CHILDREN_VARNAME);
		pto.closeTerm();
	}

	public String getLabel() {
		return label;
	}

	public List<String> getChildrenIds() {
		return childrenIds;
	}
}
