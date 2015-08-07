package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode

class Assignments extends Statement {
	List<String> assignments

	def Assignments(List<String> assignments) {
		this.assignments = assignments
	}

	def String toString() {
		toUnicode(assignments.iterator().join(" || "))
	}
}
