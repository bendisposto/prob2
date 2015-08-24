package de.prob.model.eventb.algorithm

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

	@Override
	public boolean equals(Object that) {
		if (that instanceof While) {
			return this.condition.equals(that.getCondition()) &&
			this.block.equals(that.getBlock())
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.condition.hashCode() * 7 + this.block.hashCode() * 13;
	}
}
