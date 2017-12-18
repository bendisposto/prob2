package de.prob.animator.command;

import java.util.List;

import de.prob.animator.prologast.PrologAST;
import de.prob.animator.prologast.PrologASTNode;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetMachineStructureCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_machine_formulas";
	private static final String FORMULAS = "Formulas";
	ListPrologTerm nodes;

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printVariable(FORMULAS);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.nodes = BindingGenerator.getList(bindings.get(FORMULAS));
	}

	public List<PrologASTNode> getPrologASTList(){
		PrologAST tree = new PrologAST(this.nodes);
		return tree.getASTNodes();
	}

}
