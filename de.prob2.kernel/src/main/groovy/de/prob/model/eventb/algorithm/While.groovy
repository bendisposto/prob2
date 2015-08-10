package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode

class While extends Statement {
	def String condition
	def String variant
	def Block block

	def While(String condition, String variant, Block block) {
		this.condition = condition
		this.variant = variant
		this.block = block
	}

	def String toString() {
		"while (${toUnicode(condition)}):"
	}
}
