package de.prob.model.eventb.algorithm;

import de.prob.model.eventb.DelegateHelper
import de.prob.model.representation.AbstractElement

abstract class Statement extends AbstractElement {

	def runClosure(Closure definition) {
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
