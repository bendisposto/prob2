package de.prob.model.eventb



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

	class DelegateHelper {
		def state
		def DelegateHelper(state) {
			this.state = state
		}

		def invokeMethod(String name, args) {
			state = state.invokeMethod(name, args)
		}
	}
}
