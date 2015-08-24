package de.prob.model.eventb.algorithm

class Assertion extends Statement {
	def String assertion

	def Assertion(String assertion) {
		this.assertion = assertion
	}

	def String toString() {
		"assert ${toUnicode(assertion)}"
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Assertion) {
			if (assertion != null) {
				return this.assertion.equals(that.getAssertion())
			} else {
				if (that.getAssertion() == null) {
					return true
				}
			}
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.assertion.hashCode();
	}
}
