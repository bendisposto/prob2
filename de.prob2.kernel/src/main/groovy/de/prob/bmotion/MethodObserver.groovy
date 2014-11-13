package de.prob.bmotion

import groovy.transform.TupleConstructor

@TupleConstructor
class MethodObserver extends BMotionObserver {

    def _name
    def _data

    def static MethodObserver make(Closure cls) {
        new MethodObserver().with cls
    }

    def MethodObserver name(name) {
        this._name = name
        this
    }

    def MethodObserver data(data) {
        this._data = data
        this
    }

    @Override
    def apply(BMotion bms) {
        bms.submit([cmd: _name, data: _data])
    }

}
