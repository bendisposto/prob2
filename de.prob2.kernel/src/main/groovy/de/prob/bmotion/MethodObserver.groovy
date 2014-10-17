package de.prob.bmotion

import groovy.transform.TupleConstructor

@TupleConstructor
class MethodObserver implements IBMotionObserver {

    def String method
    def data

    @Override
    def apply(BMotion bms) {
        bms.submit([cmd:method, data:data])
    }

}
