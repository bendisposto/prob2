package de.prob.model.eventb

class ParameterBlock {
	def EventParameter parameter
	def EventBGuard typingGuard

	def ParameterBlock(EventParameter parameter, EventBGuard typingGuard) {
		this.parameter = parameter
		this.typingGuard = typingGuard
	}
}
