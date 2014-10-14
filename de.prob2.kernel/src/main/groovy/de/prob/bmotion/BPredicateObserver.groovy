package de.prob.bmotion;

import groovy.transform.TupleConstructor
import de.prob.animator.domainobjects.EvalResult

@TupleConstructor
class BPredicateObserver extends TransformObserver {

	def String predicate

	def List<Transform> update(BMotion bms) {
		def result = bms.eval(predicate)
		def fpredicate = result instanceof EvalResult ? (result.value == "TRUE") : false
		return fpredicate ? super.update(bms) : []
	}
	
}