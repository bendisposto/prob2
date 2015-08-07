package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode

class Assertion extends Statement {
	def String assertion

	def Assertion(String assertion) {
		this.assertion = assertion
	}

	def String toString() {
		"assert ${toUnicode(assertion)}"
	}
}
