package de.prob.bmotion;

import de.prob.bmotion.BMotionObserver
import de.prob.bmotion.Transform

//TODO: Check if result of expression is an enumerated set
class BSetObserver extends BMotionObserver {

	def String expression
	def pattern = { it -> "#" + it }
	def transformer = new Transform()

	def BSetObserver(expression) {
		this(expression, { it -> "#" + it })
	}

	def BSetObserver(expression,pattern) {
		this.expression = expression
		this.pattern = pattern		
	}
	
	def BSetObserver set(String name,  String value) {
		transformer.attributes.put(name,value)
		this
	}

	def BSetObserver attr(String name,  String value) {
		set(name, value)
	}

	def List<Transform> update(BMotionStudioSession bms) {
		def bset = bms.eval(expression)
		def a = bset != null ? bset.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [];
		def b = a.collect{ pattern(it) }
		transformer.selector = b == []? "" : b.join(",")
		[transformer]
	}
	
}