package de.prob.model.eventb

import de.prob.model.representation.BSet

class EnumeratedSetBlock {
	BSet set
	List<EventBConstant> constants
	EventBAxiom typingAxiom

	def EnumeratedSetBlock(BSet set, List<EventBConstant> constants, EventBAxiom typingAxiom) {
		this.set = set
		this.constants = constants
		this.typingAxiom = typingAxiom
	}
}
