package de.prob.bmotion;

import groovy.transform.TupleConstructor
import de.prob.animator.domainobjects.EvalResult

@TupleConstructor(force=true)
class BPredicateObserver extends BMotionObserver {

	def String predicate
	def String selector
	private Transform transformer = new Transform()
		
	def BPredicateObserver set(String name, String value) {
		transformer.set(name,value)
		this
	}

	def BPredicateObserver attr(String name,  String value) {
		set(name, value)
	}
	
	def BPredicateObserver style(String name, String value) {
		transformer.style(name,value)
		this
	}
	
	def List<Transform> update(BMotion bms) {
		def result = bms.eval(predicate)
		def fpredicate = result instanceof EvalResult ? (result.value == "TRUE") : false
		transformer.selector = selector
		return fpredicate ? [transformer]: []
	}
	
}