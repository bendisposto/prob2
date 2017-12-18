package de.prob.animator.prologast;

import java.util.ArrayList;
import java.util.List;

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
		List<PrologASTNode> convertedNodes = new ArrayList<>();
		for (PrologTerm node : nodes) {
			convertedNodes.add(makeASTNode(node));
		}
		return convertedNodes;
	}

	private static PrologASTNode makeASTNode(PrologTerm node) {
		if (node.getFunctor().equals("formula")) {
			return new ASTFormula(node);
		} else if (node.getFunctor().equals("category")) {
			final List<PrologASTNode> subnodes = buildAST(BindingGenerator.getList(node.getArgument(3)));
			final String name = node.getArgument(1).getFunctor();
			final boolean expanded = node.getArgument(2).toString().contains("expanded");
			final boolean propagated = node.getArgument(2).toString().contains("propagated");
			return new ASTCategory(subnodes, name, expanded, propagated);
		}
		return null;
	}
}
