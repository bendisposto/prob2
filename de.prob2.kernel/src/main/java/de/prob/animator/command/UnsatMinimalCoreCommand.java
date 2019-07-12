package de.prob.animator.command;

import java.util.List;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IBEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class UnsatMinimalCoreCommand extends UnsatCoreBaseCommand {

	private static final String PROLOG_COMMAND_NAME = "get_minimum_unsat_core_with_fixed_conjuncts";
	private IBEvalElement pred;
	private List<IBEvalElement> fixedPreds;
	private static final String RESULT_VARIABLE = "UnsatCore";

	public UnsatMinimalCoreCommand(IBEvalElement pred,
			List<IBEvalElement> fixedPreds) {
		this.pred = pred;
		this.fixedPreds = fixedPreds;
	}

	@Override
	public void writeCommand(IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pred.printProlog(pout);

		pout.openList();
		for (IBEvalElement ev : fixedPreds) {
			ev.printProlog(pout);
		}
		pout.closeList();

		pout.printVariable(RESULT_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm compoundTerm = BindingGenerator.getCompoundTerm(
				bindings.get(RESULT_VARIABLE), 0);
		String code = compoundTerm.getFunctor();
		if (pred instanceof EventB) {
			core = new EventB(code, FormulaExpand.EXPAND);
		} else {
			core = new ClassicalB(code, FormulaExpand.EXPAND);
		}
	}

}
