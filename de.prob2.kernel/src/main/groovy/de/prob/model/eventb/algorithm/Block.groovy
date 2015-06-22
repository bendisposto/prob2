package de.prob.model.eventb.algorithm


class Block {
	def List<Statement> statements = []

	def Block make(Closure definition) {
		// Create clone of closure for threading access.
		Closure runClone = definition.clone()

		// Set delegate of closure to this builder.
		runClone.delegate = this

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()
		this
	}
}
