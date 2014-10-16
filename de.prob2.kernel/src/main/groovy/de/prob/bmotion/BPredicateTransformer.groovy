package de.prob.bmotion;

import groovy.transform.TupleConstructor
import de.prob.animator.domainobjects.EvalResult

@TupleConstructor
class BPredicateTransformer extends GeneralTransformer {

	def String predicate

	def List<TransformerObject> update(BMotion bms) {
		def result = bms.eval(predicate)
		def fpredicate = result instanceof EvalResult ? (result.value == "TRUE") : false
		return fpredicate ? super.update(bms) : []
	}
	
}