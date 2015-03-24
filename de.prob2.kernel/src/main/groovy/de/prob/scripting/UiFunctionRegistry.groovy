package de.prob.scripting

import com.google.inject.Singleton

@Singleton
class UiFunctionRegistry {
	
	def UiFunctionRegistry() {
		addFunction("echo", {a -> "echo:"+a});
	}
	
	def Map<String,Closure> registry = [:];
	
	def addFunction(String key, Closure code) {
		registry.put(key, code);
	}
	
	def call(name, arguments) {
		return registry.get(name).call(*arguments);
	}
	
	
	
}
