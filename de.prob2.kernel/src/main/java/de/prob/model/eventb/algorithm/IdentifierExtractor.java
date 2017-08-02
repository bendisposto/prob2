package de.prob.model.eventb.algorithm;

import java.util.HashSet;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.Node;

public class IdentifierExtractor extends DepthFirstAdapter {

	private final Set<String> identifiers;

	public IdentifierExtractor() {
		identifiers = new HashSet<>();
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

	@Override
	public void inAIdentifierExpression(AIdentifierExpression node) {
		this.identifiers.add(node.getIdentifier().getFirst().getText());
	}

	public static Set<String> union(final Node... assignments) {
		Set<String> union = new HashSet<>();
		for (Node assignment : assignments) {
			IdentifierExtractor v = new IdentifierExtractor();
			assignment.apply(v);
			union.addAll(v.getIdentifiers());
		}
		return union;
	}

	public static Set<String> intersection(final Node assignment1,
			final Node assignment2) {
		IdentifierExtractor v1 = new IdentifierExtractor();
		IdentifierExtractor v2 = new IdentifierExtractor();
		assignment1.apply(v1);
		assignment2.apply(v2);
		Set<String> intersection = new HashSet<>();
		Set<String> union = union(assignment1, assignment2);
		for (String id : union) {
			if (v1.getIdentifiers().contains(id)
					&& v2.getIdentifiers().contains(id)) {
				intersection.add(id);
			}
		}
		return intersection;
	}

	public static boolean disjoint(final Node assignment1,
			final Node assignment2) {
		return intersection(assignment1, assignment2).isEmpty();
	}
}
