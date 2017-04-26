package de.prob.animator.command;

import de.prob.animator.prologast.PrologAST;
import de.prob.animator.prologast.PrologASTNode;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import java.util.List;

public class GetMachineStructureCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_machine_formulas";
	private static final String STATE = "Formulas";
	ListPrologTerm nodes;

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printVariable(STATE);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.nodes = BindingGenerator.getList(bindings.get(STATE));
	}

	public List<PrologASTNode> getPrologASTList(){
		PrologAST tree = new PrologAST(this.nodes);
		return tree.getASTNodes();
	}

}
