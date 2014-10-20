package de.prob.bmotion

import groovy.transform.TupleConstructor

@TupleConstructor
class MethodObserver extends BMotionObserver {

    def String name
    def data

    def static MethodObserver make(Closure cls) {
        new MethodObserver().with cls
    }

    def MethodObserver name(name) {
        this.name = name
        this
    }

    def MethodObserver data(data) {
        this.data = data
        this
    }

    @Override
    def apply(BMotion bms) {
        bms.submit([cmd:name, data:data])
    }

}
