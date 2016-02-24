package de.prob.model.eventb

class DelegateHelper {
	def state
	def DelegateHelper(state) {
		this.state = state
	}

	def invokeMethod(String name, args) {
		state = state.invokeMethod(name, args)
	}
}
