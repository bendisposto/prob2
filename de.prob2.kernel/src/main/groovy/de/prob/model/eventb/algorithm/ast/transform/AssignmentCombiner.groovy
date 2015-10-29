package de.prob.model.eventb.algorithm.ast.transform

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.AssignmentAnalysisVisitor
import de.prob.model.eventb.algorithm.ast.Assignments
import de.prob.model.eventb.algorithm.ast.Statement

class AssignmentCombiner extends AlgorithmASTTransformer {

	@Override
	public List<Statement> transform(Assignments a, List<Statement> rest) {
		List<Statement> nextS = rest
		List<EventB> allAssignments = []
		allAssignments.addAll(a.assignments)
		while (nextS && nextS.first() instanceof Assignments) {
			allAssignments.addAll(nextS.first().assignments)
			nextS = nextS.tail()
		}

		List<Assignments> assignments = []

		List<EventB> actions = []
		Set<String> identifiers = [] as Set
		allAssignments.each { EventB formula ->
			AssignmentAnalysisVisitor v = new AssignmentAnalysisVisitor()
			formula.getAst().apply(v)
			boolean disjoint = v.getIdentifiers().inject(true) { acc, e -> acc && !identifiers.contains(e) }
			if (disjoint) {
				actions << formula
				identifiers.addAll(v.getIdentifiers())
			} else {
				assignments << new Assignments(actions, a.typeEnvironment)
				identifiers = v.getIdentifiers()
				actions = [formula]
			}
		}
		if (actions) {
			assignments << new Assignments(actions, a.typeEnvironment)
		}
		if (nextS) {
			assignments.addAll(transform(nextS.head(), nextS.tail()))
		}
		assignments
	}
}
