package de.prob.bmotion;

import java.util.List;

import de.prob.bmotion.BMotionObserver
import de.prob.bmotion.SelectorTransformer

//TODO: Check if result of expression is an enumerated set
class BSetObserver extends BMotionObserver {

	def String expression
	def pattern = { it -> "#" + it }
	def transformer = new SelectorTransformer()

	def BSetObserver(expression) {
		this.expression = expression
	}

	def BSetObserver(expression,pattern) {
		this.expression = expression
		this.pattern = pattern		
	}
	
	def BSetObserver set(String name,  String value) {
		transformer.set(name,value)
		this
	}

	def BSetObserver attr(String name,  String value) {
		set(name, value)
	}

	def List<SelectorTransformer> update(BMotion bms) {
		def bset = bms.eval(expression)
		def a = bset != null ? bset.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [];
		def b = a.collect{ pattern(it) }
		transformer.selector = b == []? "" : b.join(",")
		[transformer]
	}
	
}