package de.prob.model.eventb.algorithm

import de.prob.model.eventb.DelegateHelper
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.ModelElementList


class Block extends AbstractElement {
	final ModelElementList<Statement> statements

	def Block(List<Statement> statements=[]) {
		this.statements = new ModelElementList<Statement>(statements)
	}

	def Block If(String condition, Closure definition) {
		new Block(statements.addElement(new If(condition).make(definition)))
	}

	def Block While(String condition, Closure definition) {
		new Block(statements.addElement(new While(condition, new Block().make(definition))))
	}

	def Block Assert(String condition) {
		new Block(statements.addElement(new Assertion(condition)))
	}

	def Block Assign(String... assignments) {
		new Block(statements.addElement(new Assignments(assignments as List)))
	}

	def Block make(Closure definition) {
		// Create clone of closure for threading access.
		Closure runClone = definition.clone()

		def delegateH = new DelegateHelper(this)
		// Set delegate of closure to this builder.
		runClone.delegate = delegateH

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()

		delegateH.getState()
	}
}
