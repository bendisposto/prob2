package de.prob.scripting

import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.statespace.Animations
import de.prob.unicode.UnicodeTranslator;

@Singleton
class UiFunctionRegistry {

	private Animations animations;

	@Inject
	def UiFunctionRegistry(Animations animations) {
		this.animations = animations;
		addFunction("echo", {a -> "echo:"+a});
		addFunction("parse", {traceId, formula-> 
			["status":animations.getTrace(traceId).model.checkSyntax(formula), 
			 "unicode":UnicodeTranslator.toUnicode(formula), 
			 "ascii":UnicodeTranslator.toAscii(formula)];})
	}

	def Map<String,Closure> registry = [:];

	def addFunction(String key, Closure code) {
		registry.put(key, code);
	}

	def call(name, arguments) {
		return registry.get(name).call(*arguments);
	}
}
