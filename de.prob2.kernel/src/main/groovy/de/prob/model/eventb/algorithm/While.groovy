package de.prob.model.eventb.algorithm

class While implements Statement {
	def String condition
	def Block block

	def While(String condition, Block block) {
		this.condition = condition
		this.block = block
	}
}
