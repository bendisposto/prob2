package de.prob.animator.command;

import java.util.List;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IBEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class UnsatCoreCommand extends AbstractCommand {

	private static final String RESULT_VARIABLE = "UnsatCore";
	private IBEvalElement pred;
	private List<IBEvalElement> fixedPreds;
	private IBEvalElement core;

	public UnsatCoreCommand(IBEvalElement pred, List<IBEvalElement> fixedPreds) {
		this.pred = pred;
		this.fixedPreds = fixedPreds;

	}

	@Override
	public void writeCommand(IPrologTermOutput pout) {
		pout.openTerm("get_unsat_core_with_fixed_conjuncts");
		pred.printProlog(pout);
	
		pout.openList();
		for(IBEvalElement ev : fixedPreds) {
			ev.printProlog(pout);
		}
		pout.closeList();
		
		pout.printVariable(RESULT_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm compoundTerm = BindingGenerator.getCompoundTerm(bindings.get(RESULT_VARIABLE), 0);
		String code = compoundTerm.getFunctor();
		if (pred instanceof EventB) {
			core = new EventB(code);
		} else {
			core = new ClassicalB(code);
		}
	}

	public IBEvalElement getCore() {
		return core;
	}

}
