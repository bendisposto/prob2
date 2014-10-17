package de.prob.bmotion

class BMotionDefaultComponent extends BMotionComponent {

    def init(BMotion bms) {
    }

    @Override
    def registerObserver(IBMotionObserver o) {
        registerObserver([o])
    }

    @Override
    def registerObserver(List<IBMotionObserver> o) {
        observers += o
    }

}
