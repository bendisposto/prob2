package de.prob.bmotion;

import groovy.transform.TupleConstructor

//TODO: Check if result of expression is an enumerated set
@TupleConstructor
class BSetTransformer extends GeneralTransformer {

	def String expression
	def convert = { it -> "#" + it }
	def resolve = { it -> it != null ? it.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [] }

	def List<TransformerObject> update(BMotion bms) {
        def bset = bms.eval(expression)
		def a = resolve(bset)
		def b = a.collect{ convert(it) }
        selector = b == []? "" : b.join(",")
        super.update(bms)
	}
	
}