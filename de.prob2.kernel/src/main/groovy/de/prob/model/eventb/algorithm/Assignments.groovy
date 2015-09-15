package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.unicode.UnicodeTranslator

class Assignments extends Statement {
	List<EventB> assignments

	def Assignments(List<String> assignments, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.assignments = assignments.collect { parseFormula(it, EvalElementType.ASSIGNMENT) }
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
}
