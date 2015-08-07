package de.prob.model.eventb.algorithm

class While extends Statement {
	def String condition
	def Block block

	def While(String condition, Block block) {
		this.condition = condition
		this.block = block
	}
}
