package de.prob.bmotion

import de.prob.animator.domainobjects.EvalResult
import groovy.transform.TupleConstructor

@TupleConstructor
class BPredicateObserver extends TransformerObserver {

    def String predicate

    def static BPredicateObserver make(Closure cls) {
        new BPredicateObserver().with cls
    }

    def BPredicateObserver predicate(predicate) {
        this.predicate = predicate
        this
    }

    def List<TransformerObject> update(BMotion bms) {
        def result = bms.eval(predicate)
        def fp = result instanceof EvalResult ? (result.value == "TRUE") : false
        return fp ? super.update(bms) : []
    }

}