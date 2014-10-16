package de.prob.bmotion

import groovy.transform.TupleConstructor

@TupleConstructor
class MethodObserver implements IBMotionObserver {

    def String method
    def json

    @Override
    def apply(BMotion bms) {
        System.out.println("APPLY")
        bms.submit([cmd:method, data:json])
    }

}
