package de.prob.model.eventb.algorithm

class Assignments extends Statement {
	List<String> assignments

	def Assignments(List<String> assignments) {
		this.assignments = assignments
	}

	def String toString() {
		toUnicode(assignments.iterator().join(" || "))
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Assignments) {
			return this.assignments.equals(that.getAssignments())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.assignments.hashCode()
	}
}
