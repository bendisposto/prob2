package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException
import de.prob.unicode.UnicodeTranslator

class Assignments extends Statement {
	List<EventB> assignments
	Set<String> identifiers

	def Assignments(Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		assignments = []
		identifiers = [] as Set
	}

	private Assignments(List<EventB> assignments, Set<String> identifiers, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.assignments = assignments
		this.identifiers = identifiers
	}

	def String toString() {
		UnicodeTranslator.toUnicode(assignments.collect {it.getCode()}.iterator().join(" || "))
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Assignments) {
			if (this.assignments.size() != that.assignments.size()) {
				return false
			}
			return [
				this.assignments,
				that.assignments
			].transpose().inject(true) { acc, List<EventB> e ->
				acc && e[0].getCode() == e[1].getCode()
			}
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.assignments.hashCode()
	}

	public List<Assignments> addAssignments(String... assignments) throws ModelGenerationException {
		List<EventB> actions = new ArrayList<EventB>(this.assignments)
		Set<String> identifiers = new HashSet<String>(identifiers)

		List<String> fromConflict = null
		assignments.each { String assignment ->
			if (fromConflict) {
				fromConflict << assignment
			} else {
				EventB formula = parseFormula(assignment, EvalElementType.ASSIGNMENT)
				AssignmentAnalysisVisitor v = new AssignmentAnalysisVisitor()
				formula.getAst().apply(v)
				boolean disjoint = v.getIdentifiers().inject(true) { acc, e -> acc && !identifiers.contains(e) }
				if (disjoint) {
					actions << formula
					identifiers.addAll(v.getIdentifiers())
				} else {
					fromConflict = [assignment]
				}
			}
		}
		List<Assignments> acts = [
			new Assignments(actions, identifiers, typeEnvironment)
		]
		if (fromConflict) {
			acts.addAll(new Assignments(typeEnvironment).addAssignments(fromConflict as String[]))
		}
		acts
	}
}
