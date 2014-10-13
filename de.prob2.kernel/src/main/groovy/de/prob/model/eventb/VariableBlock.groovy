package de.prob.model.eventb;

public class VariableBlock {
	EventBVariable variable
	EventBInvariant typingInvariant
	EventBAction initialisationAction

	def VariableBlock(final EventBVariable variable, final EventBInvariant typingInvariant, EventBAction initAction) {
		this.variable = variable
		this.typingInvariant = typingInvariant
		this.initialisationAction = initAction
	}
}
