package de.prob.bmotion

class BMotionDefaultComponent extends BMotionComponent {

    def TransformerObserver transformerObserver

    def init(BMotion bms) {
        transformerObserver = new TransformerObserver()
        observers.add(transformerObserver)
    }

    @Override
    def registerObserver(IBMotionObserver o) {
        registerObserver([o])
    }

    @Override
    def registerObserver(List<IBMotionObserver> o) {
        o.each {
            (it instanceof IBMotionTransformer) ? transformerObserver.transformers.add(it) : observers.add(it)
        }
    }

}
