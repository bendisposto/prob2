package de.prob.bmotion

class BMotionDefaultComponent extends BMotionComponent {

    def init(BMotion bms) {
    }

    @Override
    def registerObserver(BMotionObserver o) {
        registerObserver([o])
    }

    @Override
    def registerObserver(List<BMotionObserver> o) {
        observers += o
    }

}
