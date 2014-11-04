package de.prob.bmotion

import com.google.gson.Gson

class TransformersObserver extends BMotionObserver {

    def Gson g = new Gson()

    def List<BMotionTransformer> transformers = []

    def static TransformersObserver make(Closure cls) {
        new TransformersObserver().with cls
    }

    def TransformersObserver add(BMotionTransformer transformer) {
        transformers.add(transformer)
        this
    }

    @Override
    def apply(BMotion bms) {
        String json = g.toJson(transformers.collectMany { it.update(bms) })
        bms.submit([cmd: "bms.applyTransformers", transformers: json])
    }

}
