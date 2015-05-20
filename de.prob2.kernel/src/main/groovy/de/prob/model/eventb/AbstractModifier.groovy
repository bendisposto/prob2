package de.prob.model.eventb

import groovy.lang.Closure;

class AbstractModifier {
	
	protected Map validateProperties(Map properties, Map required) {
		required.collectEntries { prop,type ->
			if(!properties[prop]) {
				throw new IllegalArgumentException("Could not find required property $prop in definition")
			}
			try {
				return [prop, properties[prop].asType(type)]
			} catch(Exception e) {
				throw new IllegalArgumentException("Expected property $prop to have type $type")
			}
		}
	}
	
	protected getDefinition(Map definition) {
		new Definition(definition)
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
