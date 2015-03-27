package de.prob.scripting

import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.statespace.Animations

@Singleton
class UiFunctionRegistry {

	private Animations animations;

	@Inject
	def UiFunctionRegistry(Animations animations) {
		this.animations = animations;
		addFunction("echo", {a -> "echo:"+a});
		addFunction("parse", {traceId, formula-> animations.getTrace(traceId).model.checkSyntax(formula);})
	}

	def Map<String,Closure> registry = [:];

	def addFunction(String key, Closure code) {
		registry.put(key, code);
	}

	def call(name, arguments) {
		return registry.get(name).call(*arguments);
	}
}
