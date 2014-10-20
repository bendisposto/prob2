package de.prob.bmotion;

import groovy.transform.TupleConstructor

//TODO: Check if result of expression is an enumerated set
@TupleConstructor
class BSetObserver extends TransformerObserver {

	def String expression
	def convertfn = { "#" + it }
	def resolvefn = { it != null ? it.value.replace("{","").replace("}","").replaceAll(" ","").tokenize(",") : [] }

    def static BSetObserver make(Closure cls) {
        new BSetObserver().with cls
    }

    def BSetObserver expression(exp) {
        this.expression = exp
        this
    }

    def BSetObserver convert(Closure cls) {
        this.convertfn = clss
        this
    }

    def BSetObserver resolve(Closure cls) {
        this.resolvefn = cls
        this
    }

    def List<TransformerObject> update(BMotion bms) {
        def a = resolvefn(bms.eval(expression))
        def b = a.collect{ convertfn(it) }
        selector = b == []? "" : b.join(",")
        super.update(bms)
    }

}