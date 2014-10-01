package de.prob.bmotion;

import de.prob.bmotion.BMotionObserver
import de.prob.bmotion.Transform

//TODO: Check if result of expression is an enumerated set
class BSetObserver extends BMotionObserver {

	def String expression
	def pattern = { it -> "#" + it }
	def List<Attribute> attributes = [];

	def BSetObserver(expression) {
		this(expression, { it -> "#" + it })
	}

	def BSetObserver(expression,pattern) {
		this.expression = expression
		this.pattern = pattern		
	}
	
	def BSetObserver set(String name, String value) {
		attributes << new Attribute(name, value);
		this
	}

	def List<Transform> update(BMotionStudioSession bms) {
		def bset = bms.eval(expression)
		def a = bset != null ? bset.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [];
		def b = a.collect{ pattern(it) }
		[new Transform(b == []? "" : b.join(","), attributes)]
	}
	
}