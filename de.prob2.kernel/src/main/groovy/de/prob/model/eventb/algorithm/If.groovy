package de.prob.model.eventb.algorithm

import de.prob.model.representation.IllegalModificationException


class If implements Statement {
	def String condition
	def Block Then
	def Block Else

	def If(String condition) {
		this.condition = condition
	}

	def If(String condition, Block Then, Block Else) {
		this.condition = condition
		this.Then = Then
		this.Else = Else
	}

	def If Then(String... assignments) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		Then = new Block(assignments.collect { new Assignment(it)})
		this
	}

	def If Then(Closure definition) {
		if(Then != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		Then = new Block().make definition
		this
	}

	def If Else(String... assignments) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		Else = new Block(assignments.collect { new Assignment(it)})
		this
	}

	def If Else(Closure definition) {
		if(Else != null) {
			throw new IllegalModificationException("The Then block of this If statement has already been defined. Cannot be redefined.")
		}
		Else = new Block().make definition
		this
	}
	def If make(Closure definition) {
		// Create clone of closure for threading access.
		Closure runClone = definition.clone()

		// Set delegate of closure to this builder.
		runClone.delegate = this

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()

		// ensure that Then and Else are set
		Then = Then ?: new Block()
		Else = Else ?: new Block()
		this
	}
}
