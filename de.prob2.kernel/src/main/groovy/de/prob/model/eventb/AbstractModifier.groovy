package de.prob.model.eventb

import groovy.lang.Closure;

class AbstractModifier {
	
	protected hasProperties(Map properties, List required) {
		required.each { prop ->
			if(!properties[prop]) {
				throw new IllegalArgumentException("Could not find required property $prop in definition")
			}
		}
	}

	protected runClosure(Closure runClosure) {
		// Create clone of closure for threading access.
		Closure runClone = runClosure.clone()

		// Set delegate of closure to this builder.
		runClone.delegate = this

		// And only use this builder as the closure delegate.
		runClone.resolveStrategy = Closure.DELEGATE_ONLY

		// Run closure code.
		runClone()
	}
}
