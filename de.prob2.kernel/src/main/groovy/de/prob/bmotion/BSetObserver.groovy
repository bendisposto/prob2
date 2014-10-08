package de.prob.bmotion;

import java.util.List;

import de.prob.bmotion.BMotionObserver
import de.prob.bmotion.SelectorTransformer
import groovy.transform.TupleConstructor

//TODO: Check if result of expression is an enumerated set
@TupleConstructor(force=true)
class BSetObserver extends BMotionObserver {

	def String expression
	def resolve = { it -> it != null ? it.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [] }
	def convert = { it -> "#" + it }	
	private SelectorTransformer transformer = new SelectorTransformer()
		
	def BSetObserver set(String name, String value) {
		transformer.set(name,value)
		this
	}

	def BSetObserver attr(String name,  String value) {
		set(name, value)
	}
	
	def BSetObserver style(String name, String value) {
		transformer.style(name,value)
		this
	}
	
	def List<SelectorTransformer> update(BMotion bms) {
		def bset = bms.eval(expression)
		def a = resolve(bset)
		def b = a.collect{ convert(it) }
		transformer.selector = b == []? "" : b.join(",")
		[transformer]
	}
	
}