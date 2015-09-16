package de.prob.model.eventb.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AAssignSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;

public class AssignmentAnalysisVisitor extends DepthFirstAdapter {

	private final Set<String> identifiers;

	public AssignmentAnalysisVisitor() {
		identifiers = new HashSet<String>();
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

	@Override
	public void inAAssignSubstitution(final AAssignSubstitution node) {
		addAll(node.getLhsExpression());
	}

	@Override
	public void inABecomesSuchSubstitution(final ABecomesSuchSubstitution node) {
		addAll(node.getIdentifiers());
	}

	@Override
	public void inABecomesElementOfSubstitution(
			final ABecomesElementOfSubstitution node) {
		addAll(node.getIdentifiers());
	}

	public void addAll(final LinkedList<PExpression> identifiers) {
		for (PExpression pExpression : identifiers) {
			if (pExpression instanceof AIdentifierExpression) {
				this.identifiers.add(((AIdentifierExpression) pExpression)
						.getIdentifier().getFirst().getText());
			}
		}
	}

	public static Set<String> union(final Node... assignments) {
		Set<String> union = new HashSet<String>();
		for (Node assignment : assignments) {
			AssignmentAnalysisVisitor v = new AssignmentAnalysisVisitor();
			assignment.apply(v);
			union.addAll(v.getIdentifiers());
		}
		return union;
	}

	public static Set<String> intersection(final Node assignment1,
			final Node assignment2) {
		AssignmentAnalysisVisitor v1 = new AssignmentAnalysisVisitor();
		AssignmentAnalysisVisitor v2 = new AssignmentAnalysisVisitor();
		assignment1.apply(v1);
		assignment2.apply(v2);
		Set<String> intersection = new HashSet<String>();
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
