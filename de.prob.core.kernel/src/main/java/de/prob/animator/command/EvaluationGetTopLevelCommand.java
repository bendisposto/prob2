package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.CachedFormula;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command retrieves the IDs of the top-level expressions and their labels
 * and the IDs of their children.
 * 
 * @see EvaluationExpandCommand
 * @see EvaluationGetValuesCommand
 * @author plagge
 */
public class EvaluationGetTopLevelCommand extends AbstractCommand {

	public final String TOPS = "Tops";
	private final List<CachedFormula> topLevel = new ArrayList<CachedFormula>();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_get_top_level");
		pto.printVariable(TOPS);
		pto.closeTerm();

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TOPS);
		ListPrologTerm pts = BindingGenerator.getList(prologTerm);
		for (PrologTerm p : pts) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(p, 3);
			ListPrologTerm list = BindingGenerator.getList(cpt.getArgument(3));
			List<String> chIds = new ArrayList<String>();
			for (PrologTerm pt2 : list) {
				chIds.add(pt2.getFunctor());
			}
			topLevel.add(new CachedFormula(cpt.getArgument(1).getFunctor(), cpt
					.getArgument(2).getFunctor(), chIds));
		}
	}

	public List<CachedFormula> getTopLevel() {
		return topLevel;
	}

}
