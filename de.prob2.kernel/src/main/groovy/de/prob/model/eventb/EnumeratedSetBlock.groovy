package de.prob.model.eventb

import de.prob.model.representation.Set

class EnumeratedSetBlock {
	Set set
	List<EventBConstant> constants
	EventBAxiom typingAxiom

	def EnumeratedSetBlock(Set set, List<EventBConstant> constants, EventBAxiom typingAxiom) {
		this.set = set
		this.constants = constants
		this.typingAxiom = typingAxiom
	}
}
