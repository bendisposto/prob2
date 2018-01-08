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
			final PrologTerm term = node.getArgument(1);
			final PrologTerm prettyPrintTerm = node.getArgument(2);
			// FIXME Are integers supposed to be allowed as pretty prints?
			// Currently, the CLI uses integers as pretty prints in a few cases, such as animation functions.
			// It's not clear if this is the intended behavior, or if they should have been converted to atoms in the Prolog code.
			if (!prettyPrintTerm.isAtom() && !prettyPrintTerm.isNumber()) {
				throw new IllegalArgumentException("Formula pretty print must be an atom or number: " + prettyPrintTerm);
			}
			final String prettyPrint = prettyPrintTerm.getFunctor();
			return new ASTFormula(term, prettyPrint);
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
