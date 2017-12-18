package de.prob.animator.prologast;

import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * The left node is the category given by Prolog, the right node is a formula or
 * another category inside the root-category
 */
public final class PrologAST {
	private PrologAST() {}

	public static List<PrologASTNode> buildAST(ListPrologTerm nodes) {
		return nodes.stream().map(PrologAST::makeASTNode).collect(Collectors.toList());
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
}
