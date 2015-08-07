package de.prob.model.eventb



class AbstractModifier {

	protected Map validateProperties(Map properties, Map required) {
		required.collectEntries { String prop,type ->
			if (type instanceof List && type.size() == 2) {
				return validateOptionalProperty(properties, prop, type)
			}
			if (type instanceof Class) {
				return validateRequiredProperty(properties, prop, type)
			}
			throw new IllegalArgumentException("incorrect properties: values must be either a class or a tuple with two elements")
		}
	}

	protected validateOptionalProperty(LinkedHashMap properties, String property, List type) {
		if (properties[property]) {
			return [property, properties[property].asType(type[0])]
		} else {
			return [property, type[1]]
		}
	}

	protected validateRequiredProperty(LinkedHashMap properties, String property, Class type) {
		if (properties[property]) {
			return [property, properties[property].asType(type)]
		} else {
			throw new IllegalArgumentException("Expected property $property to have type $type")
		}
	}

	protected getDefinition(Map definition) {
		new Definition(definition)
	}

	def static int extractCounter(String prefix, List elements) {
		int counter = -1
		elements.each { e ->
			if (e.getName() ==~ "$prefix[0-9]+") {
				def str = e.getName().replace(prefix, "")
				int cnt = str as Integer
				if (cnt > counter) {
					counter = cnt
				}
			}
		}
		counter
	}

	protected AbstractModifier runClosure(Closure runClosure) {
		// Create clone of closure for threading access.
		Closure runClone = runClosure.clone()

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
