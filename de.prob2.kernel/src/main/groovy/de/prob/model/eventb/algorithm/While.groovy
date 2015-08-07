package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode

class While extends Statement {
	def String condition
	def Block block

	def While(String condition, Block block) {
		this.condition = condition
		this.block = block
	}

	def String toString() {
		"while (${toUnicode(condition)}):"
	}
}
