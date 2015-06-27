package de.prob.model.eventb.algorithm


class Block {
	def List<Statement> statements = []

	def Block(List<Statement> statements=[]) {
		this.statements = statements
	}

	def Block If(String condition, Closure definition) {
		statements << new If(condition).make(definition)
		this
	}

	def Block While(String condition, Closure definition) {
		statements << new While(condition, new Block().make(definition))
		this
	}

	def Block Assert(String condition) {
		statements << new Assertion(condition)
		this
	}

	def Block Assign(String... assignments) {
		assignments.each {
			statements << new Assignment(it)
		}
		this
	}

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
