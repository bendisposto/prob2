package de.prob.animator.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.prologast.ASTCategory;
import de.prob.animator.prologast.ASTFormula;
import de.prob.animator.prologast.PrologASTNode;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetMachineStructureCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_machine_formulas";
	private static final String FORMULAS = "Formulas";

	private List<PrologASTNode> rootNodes;

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printVariable(FORMULAS);
		pto.closeTerm();
	}

	private static List<PrologASTNode> buildAST(ListPrologTerm nodes) {
		return nodes.stream().map(GetMachineStructureCommand::makeASTNode).collect(Collectors.toList());
	}

	private static PrologASTNode makeASTNode(PrologTerm node) {
		if ("formula".equals(node.getFunctor())) {
			return new ASTFormula(node);
		} else if ("category".equals(node.getFunctor())) {
			final String name = node.getArgument(1).getFunctor();
			final List<String> infos = PrologTerm.atomicStrings((ListPrologTerm)node.getArgument(2));
			final List<PrologASTNode> subnodes = buildAST(BindingGenerator.getList(node.getArgument(3)));
			final boolean expanded = infos.contains("expanded");
			final boolean propagated = infos.contains("propagated");
			return new ASTCategory(subnodes, name, expanded, propagated);
		} else {
			throw new AssertionError("Unknown node type: " + node.getFunctor());
		}
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.rootNodes = buildAST(BindingGenerator.getList(bindings.get(FORMULAS)));
	}

	public List<PrologASTNode> getPrologASTList() {
		return Collections.unmodifiableList(this.rootNodes);
	}
}
